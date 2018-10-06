package io.egia.mqi.server;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;

@RunWith(MockitoJUnitRunner.class)
public class ServerServiceTest {

    @Mock
    private ServerRepo serverRepo;

    private ServerService serverService;

    private static Server server;
    static {
        server = Server.builder().serverName("Fake Server").systemType(SystemType.PRIMARY).build();
    }

    @Before
    public void setUp() {
        serverService = new ServerService(serverRepo);
    }

    @Test
    public void returnsThisServersHostName() throws UnknownHostException {
        String thisServersName = InetAddress.getLocalHost().getHostName();
        assertThat(ServerService.thisServersHostName()).isEqualTo(thisServersName);
    }

    @Test
    public void returnsServerFromHostNameAndPort() throws UnknownHostException {
        Mockito.when(serverRepo.findOneByServerNameAndServerPort(anyString(), anyString())).thenReturn(server);
        assertThat(serverService.getServerFromHostNameAndPort("8080")).isEqualTo(server);
    }

    @Test
    public void returnsPrimaryServer() {
        Mockito.when(serverRepo.findOneBySystemType(SystemType.PRIMARY)).thenReturn(server);
        assertThat(serverService.getPrimaryServer()).isEqualTo(server);
    }
}
