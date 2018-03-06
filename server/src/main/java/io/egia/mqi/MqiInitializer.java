package io.egia.mqi;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import io.egia.mqi.domain.Server;
import io.egia.mqi.domain.ServerRepository;
import io.egia.mqi.domain.Version;
import io.egia.mqi.domain.VersionRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author vango
 *
 *         The purpose of this class is to verify that the application server
 *         version and the database version are in sync. If the database is out
 *         of date, we will call the DatabaseManager to bring it up the current
 *         version of the software. If the application server is out of date,
 *         then it will shutdown (the user will need to install the most recent
 *         version).
 * 
 */

@Component
public class MqiInitializer implements ApplicationListener<ContextRefreshedEvent> {

	private Logger log = LoggerFactory.getLogger(MqiInitializer.class);

	@Autowired
	private VersionRepository versionRepository;

	@Autowired
	private ServerRepository serverRepository;

	@Autowired
	private DatabaseManager dbManager;

	@Value("${mqi.properties.home.directory}")
	private String homeDirectory;

	@Value("${server.port}")
	private String serverPort;

	@Value("${mqi.properties.server.type}")
	private String serverType;

	@Value("${mqi.properties.server.version}")
	private String serverVersion;

	private String currentDatabaseVersion;

	private String serverName;

	InetAddress serverIp;

	private List<Version> versions = new ArrayList<Version>();

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		try {

			currentDatabaseVersion = retrieveCurrentDatabaseVersion();

			dbManager.setVersionsDirectory(homeDirectory + File.separator + "versions");

			initializeMqi(this.currentDatabaseVersion, this.serverVersion);

			log.info("--------------------------------------------------");
			log.info("Egia Software Solutions, Inc");
			log.info("Medical Quality Informatics ");
			log.info(String.format("v%s", this.serverVersion));
			log.info(String.format("Server Type: %s", this.serverType));
			log.info(String.format("Home Direcotry: %s", this.homeDirectory));
			log.info("--------------------------------------------------");
		} catch (MqiExceptions e) {
			e.printStackTrace();
		}
	}

	private String retrieveCurrentDatabaseVersion() throws MqiExceptions {

		List<Version> version = versionRepository.findAll();

		if (version.size() == 1) {
			return version.get(0).getVersionId();
		} else if (version.size() > 1) {
			// This error should never occur, we expect only one record in the
			// Version Repository.
			throw new MqiExceptions("There is more than one software version listed in the database.");
		} else {
			// If there is no version in the database, we will insert version
			// "0.0.0"
			// This was done so when the standalone configuration creates an
			// hsql db, a version is inserted.
			Version v = new Version();
			v.setVersionId("0.0.0");
			versionRepository.saveAndFlush(v);
			return v.getVersionId();
		}
	}

	public void initializeMqi(String currentDatabaseVersion, String currentServerVersion) throws MqiExceptions {
		Version currDbVer = new Version();
		Version currSrvVer = new Version();

		currDbVer.setVersionId(currentDatabaseVersion);
		currSrvVer.setVersionId(currentServerVersion);

		// If the database version is out of date, apply the necessary updates
		if (currSrvVer.compareTo(currDbVer) > 0) {
			log.info("MQi database is not the latest version.");
			// Only the primary application server may apply database updates
			if (serverType.equalsIgnoreCase("primary")) {

				log.info("Performing database update.");
				versions = retrieveVersions();
				// Loop through all the versions in the updates directory
				for (Version v : versions) {
					// if the version is newer than the current version, apply
					// the update
					if (v.compareTo(currDbVer) > 0) {
						dbManager.applyVersion(v);
					}
				}
			} else {
				throw new MqiExceptions(
						"Invalid database version. Update database by upgrading and starting a primary MQi server.");
			}
		} else if (currSrvVer.compareTo(currDbVer) == 0) {
			log.info("Database and application server versions are in sync.");
		} else {
			throw new MqiExceptions("This MQi server is not the latest version, please upgrade MQi.");
		}

		// Update the t_server table with this servers information;
		// The installer will prevent two primary servers from being added to
		// the same environment. But the application verifies that's the case.
		try {
			serverIp = InetAddress.getLocalHost();
			serverName = serverIp.getHostName();
			
			List<Server> thisServer = serverRepository.findByServerNameAndServerPort(serverName, serverPort);
			List<Server> primaryServer = serverRepository.findByServerType("primary");
			
			Server s = new Server();
			s.setServerName(serverName);
			s.setServerPort(serverPort);
			s.setServerType(serverType);
			s.setServerVersion(serverVersion);

			if (serverType.equals("primary") && primaryServer.size() <= 1) {
				if (thisServer.size() == 1) {
					log.info("This server already exists in the t_server table, updating entry.");
					serverRepository.updateServer(thisServer.get(0).getServerId(), serverType, serverVersion);
				} else if (thisServer.size() < 1 && primaryServer.size() != 1) {
					log.info("Primary server does not exist in the t_server table, adding entry.");
					serverRepository.saveAndFlush(s);
				}
			} else if (serverType.equals("secondary")) {
				if (thisServer.size() == 1) {
					log.info("This server already exists in the t_server table, updating entry.");
					serverRepository.updateServer(thisServer.get(0).getServerId(), serverType, serverVersion);
				} else if (thisServer.size() < 1) {
					log.info("Secondary server does not exist in the t_server table, adding entry.");
					serverRepository.saveAndFlush(s);
				} else {
					throw new MqiExceptions("There is more than one entry for this server in the t_server table. Please remove one of the entries.");
				}
			} else if (primaryServer.size() <= 2){
				throw new MqiExceptions("There is more than one primary server entry in the t_server table. Please remove one of the entries");
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	private List<Version> retrieveVersions() {
		log.info("Retrieving versions from home/updates directory");

		Path updatesPath = FileSystems.getDefault().getPath(homeDirectory + File.separator + "versions");

		try (DirectoryStream<Path> stream = Files.newDirectoryStream(updatesPath)) {
			for (Path file : stream) {
				Version v = new Version();
				v.setVersionId(file.getFileName().toString());
				versions.add(v);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		versions.sort((vA, vB) -> vA.compareTo(vB));

		return versions;
	}
}
