package io.egia.mqi;

import io.egia.mqi.measure.RuleParamUtility;
import io.egia.mqi.server.Server;
import io.egia.mqi.server.ServerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.net.UnknownHostException;

@Component
public class MqiInitializer implements ApplicationListener<ContextRefreshedEvent> {
	private Logger log = LoggerFactory.getLogger(MqiInitializer.class);
	private ServerService serverService;
	private RuleParamUtility ruleParamUtility;

	@Value("${server.port}")
	private String serverPort;

	@Value("${mqi.properties.system.type}")
	private String systemType;

	@Value("${mqi.properties.system.version}")
	private String systemVersion;

	MqiInitializer(ServerService serverService, RuleParamUtility ruleParamUtility) {
		this.serverService = serverService;
		this.ruleParamUtility = ruleParamUtility;
	}

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		try {
			initializeMqi();

			ruleParamUtility.saveRuleParams();

			log.info("--------------------------------------------------");
			log.info("Egia Software Solutions, Inc");
			log.info("Medical Quality Informatics ");
			log.info(String.format("v%s", this.systemVersion));
			log.info(String.format("server Type: %s", this.systemType));
			log.info("--------------------------------------------------");
		} catch (ClassNotFoundException | MqiExceptions e) {
			e.printStackTrace();
		}
	}

	private void initializeMqi() throws MqiExceptions {
		// Update the server table with this servers information;
		// The installer will prevent two primary servers from being added to
		// the same environment. But the application verifies that's the case.
		try {
			Server thisServer = serverService.getServerFromHostNameAndPort(serverPort);
			Server primaryServer = serverService.getPrimaryServer();

			if (systemType.equals("primary")) {
				if (thisServer != null && primaryServer != null) {
					log.info("This server already exists in the server table, updating entry.");
					serverService.updateSystemTypeAndVersion(thisServer, systemType, systemVersion);
				} else if (thisServer == null && primaryServer == null) {
					log.info("Primary server does not exist in the server table, adding entry.");
					serverService.saveServer(buildServer(ServerService.thisServersHostName()));
				}
			} else if (systemType.equals("secondary")) {
				if (thisServer != null) {
					log.info("This server already exists in the server table, updating entry.");
                    serverService.updateSystemTypeAndVersion(thisServer, systemType, systemVersion);
				} else {
					log.info("Secondary server does not exist in the server table, adding entry.");
                    serverService.saveServer(buildServer(ServerService.thisServersHostName()));
				}
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	private Server buildServer(String hostName) {
        return Server.builder()
				.serverName(hostName)
				.serverPort(serverPort)
				.systemType(systemType)
				.systemVersion(systemVersion)
				.build();
    }

}
