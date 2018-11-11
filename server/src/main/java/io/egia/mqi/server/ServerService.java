package io.egia.mqi.server;

import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static io.egia.mqi.server.SystemType.PRIMARY;

@Service
public class ServerService {
    private ServerRepo serverRepo;
    ServerService(ServerRepo serverRepo) {
        this.serverRepo = serverRepo;
    }

    public static String thisServersHostName() throws UnknownHostException {
        return getHostName();
    }

    public Server getServerFromHostNameAndPort(String serverPort) throws UnknownHostException {
        String thisServersName = getHostName();
         return this.serverRepo.findOneByServerNameAndServerPort(thisServersName, serverPort);
    }

    private static String getHostName() throws UnknownHostException {
        return InetAddress.getLocalHost().getHostName();
    }

    public Server getPrimaryServer(){
        return serverRepo.findOneBySystemType(PRIMARY);
    }

    public void saveServer(Server server) {
        serverRepo.saveAndFlush(server);
    }

    public void updateSystemTypeAndVersion(Server server, SystemType type, String verstion) {
        serverRepo.updateServer(server.getServerId(), type, verstion);
    }
}
