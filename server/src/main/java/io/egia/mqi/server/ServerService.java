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
        return getHostName();
    }

    public Server getServerFromHostNameAndPort(String serverPort) throws UnknownHostException {
        String thisServersName = getHostName();
        return this.serverRepository.findOneByServerNameAndServerPort(thisServersName, serverPort);
    }

    private static String getHostName() throws UnknownHostException {
        return InetAddress.getLocalHost().getHostName();
    }

    public Server getPrimaryServer(){
        return serverRepository.findOneBySystemType("primary");
    }

    public void saveServer(Server server) {
        serverRepository.save(server);
    }

    public void updateSystemTypeAndVersion(Server server, String type, String verstion) {
        serverRepository.updateServer(server.getServerId(), type, verstion);
    }
}
