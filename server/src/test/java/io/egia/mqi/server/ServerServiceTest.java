package io.egia.mqi.server;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static io.egia.mqi.server.SystemType.PRIMARY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;

@RunWith(MockitoJUnitRunner.class)
public class ServerServiceTest {

    @Mock private ServerRepo serverRepo;
    private ServerService serverService;

    private static Server server;
    static {
        server = Server.builder().serverName("Fake Server").systemType(PRIMARY).build();
    }

    @Before
    public void setUp() {
        serverService = new ServerService(serverRepo);
    }

    @Test
    public void returns_this_servers_hostname() throws UnknownHostException {
        String thisServersName = InetAddress.getLocalHost().getHostName();
        assertThat(ServerService.thisServersHostName()).isEqualTo(thisServersName);
    }

    @Test
    public void returns_server_from_hostname_and_port() throws UnknownHostException {
        Mockito.when(serverRepo.findOneByServerNameAndServerPort(anyString(), anyString())).thenReturn(server);
        assertThat(serverService.getServerFromHostNameAndPort("8080")).isEqualTo(server);
    }

    @Test
    public void returns_primary_server() {
        Mockito.when(serverRepo.findOneBySystemType(PRIMARY)).thenReturn(server);
        assertThat(serverService.getPrimaryServer()).isEqualTo(server);
    }
}
