package io.egia.mqi;

import io.egia.mqi.server.Server;
import io.egia.mqi.server.ServerService;
import io.egia.mqi.version.Version;
import io.egia.mqi.version.VersionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.UnknownHostException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static java.nio.file.LinkOption.NOFOLLOW_LINKS;

@Component
public class MqiInitializer implements ApplicationListener<ContextRefreshedEvent> {
	private Logger log = LoggerFactory.getLogger(MqiInitializer.class);
	private VersionRepository versionRepository;
	private DatabaseManager dbManager;
	private ServerService serverService;
	private RuleParamUtility ruleParamUtility;

	@Value("${mqi.properties.home.directory}")
	private String homeDirectory;

	@Value("${server.port}")
	private String serverPort;

	@Value("${mqi.properties.server.type}")
	private String serverType;

	@Value("${mqi.properties.server.version}")
	private String serverVersion;

	private String currentDatabaseVersion;

	private List<Version> versions = new ArrayList<Version>();

	private VersionUtility versionUtility;

	public MqiInitializer(VersionRepository versionRepository
				, DatabaseManager databaseManager
				, ServerService serverService
				, RuleParamUtility ruleParamUtility
				, VersionUtility versionUtility) {
		this.versionRepository = versionRepository;
		this.dbManager = databaseManager;
		this.serverService = serverService;
		this.ruleParamUtility = ruleParamUtility;
		this.versionUtility = versionUtility;
	}

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		try {

			currentDatabaseVersion = retrieveCurrentDatabaseVersion();

			dbManager.setVersionsDirectory(homeDirectory + File.separator + "versions");

			initializeMqi(this.currentDatabaseVersion, this.serverVersion);

			ruleParamUtility.saveRuleParams();

			log.info("--------------------------------------------------");
			log.info("Egia Software Solutions, Inc");
			log.info("Medical Quality Informatics ");
			log.info(String.format("v%s", this.serverVersion));
			log.info(String.format("server Type: %s", this.serverType));
			log.info(String.format("Home Direcotry: %s", this.homeDirectory));
			log.info("--------------------------------------------------");
		} catch (ClassNotFoundException | MqiExceptions e) {
			e.printStackTrace();
		}
	}

	private String retrieveCurrentDatabaseVersion() throws MqiExceptions {

		List<Version> version = null;

		//TODO: Remove this method when releasing initial version of software
        dbManager.dropVersionTable();

		try {
            version = versionRepository.findAll();
        } catch(Exception e) {
            log.info("version table does not exist");
            dbManager.createVersionTable();
            version = versionRepository.findAll();
        }

		if (version.size() == 1) {
			return version.get(0).getVersionId();
		} else if (version.size() > 1) {
			// This error should never occur, we expect only one record in the
			// version Repository.
			throw new MqiExceptions("There is more than one software version listed in the database.");
		} else {
			// If there is no version in the database, we will insert version
			// "0.0.0"
			// This was done so when the standalone configuration creates an
			// hsql db, a version is inserted.
			Version v = new Version("0.0.0");
			versionRepository.save(v);
			return v.getVersionId();
		}
	}

	private void initializeMqi(String currentDatabaseVersion, String currentServerVersion) throws MqiExceptions {
		Version currDbVer = new Version(currentDatabaseVersion);
		Version currSrvVer = new Version(currentServerVersion);

		// If the database version is out of date, apply the necessary updates
		if (currSrvVer.compareTo(currDbVer) > 0) {
			log.info("MQi database is not the latest version.");
			// Only the primary application server may apply database updates
			if (serverType.equalsIgnoreCase("primary")) {

				log.info("Performing database update.");
				versions = versionUtility.retrieveVersions(homeDirectory);
				// Loop through all the versions in the updates directory
				for (Version v : versions) {
					// if the version is newer than the current version, apply
					// the update
					if (v.compareTo(currDbVer) > 0) {
						versionRepository.save(dbManager.applyVersion(v));
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

		// Update the server table with this servers information;
		// The installer will prevent two primary servers from being added to
		// the same environment. But the application verifies that's the case.
		try {
			Server thisServer = serverService.getServerFromHostNameAndPort(serverPort);
			Server primaryServer = serverService.getPrimaryServer();

			if (serverType.equals("primary")) {
				if (thisServer != null && primaryServer != null) {
					log.info("This server already exists in the server table, updating entry.");
					serverService.updateServerTypeAndVersion(thisServer, serverType, serverVersion);
				} else if (thisServer == null && primaryServer == null) {
					log.info("Primary server does not exist in the server table, adding entry.");
					serverService.saveServer(buildNewServer(ServerService.thisServersHostName()));
				}
			} else if (serverType.equals("secondary")) {
				if (thisServer != null) {
					log.info("This server already exists in the server table, updating entry.");
                    serverService.updateServerTypeAndVersion(thisServer, serverType, serverVersion);
				} else if (thisServer == null) {
					log.info("Secondary server does not exist in the server table, adding entry.");
                    serverService.saveServer(buildNewServer(ServerService.thisServersHostName()));
				} else {
					throw new MqiExceptions("There is more than one entry for this server in the server table. " +
                            "Please remove one of the entries.");
				}
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	private Server buildNewServer(String hostName) {
        Server server = new Server();
        server.setServerName(hostName);
        server.setServerPort(serverPort);
        server.setServerType(serverType);
        server.setServerVersion(serverVersion);
        return server;
    }

}
