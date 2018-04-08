package io.egia.mqi.server;

import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Service
public class ServerService {
    private ServerRepository serverRepository;
    public ServerService(ServerRepository serverRepository) {
        this.serverRepository = serverRepository;
    }

    public static String thisServersHostName() throws UnknownHostException {
        return InetAddress.getLocalHost().getHostName();
    }

    public Server getServerFromHostNameAndPort(String serverPort) throws UnknownHostException {
        String thisServersName = InetAddress.getLocalHost().getHostName();
        return this.serverRepository.findOneByServerNameAndServerPort(thisServersName, serverPort);
    }

    public Server getPrimaryServer(){
        return serverRepository.findOneByServerType("primary");
    }

    public void saveServer(Server server) {
        serverRepository.save(server);
    }

    public void updateServerTypeAndVersion(Server server, String type, String verstion) {
        serverRepository.updateServer(server.getServerId(), type, verstion);
    }
}
