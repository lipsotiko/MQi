package io.egia.mqi;

import io.egia.mqi.server.ServerService;
import io.egia.mqi.version.Version;
import io.egia.mqi.version.VersionRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class MqiInitializerTest {

    @Mock
    private VersionRepository versionRepository;
    @Mock
    private DatabaseManager databaseManager;
    @Mock
    private ServerService serverService;
    @Mock
    private RuleParamUtility ruleParamUtility;
    @Mock
    private ContextRefreshedEvent contextRefreshedEvent;

    @Captor
    private ArgumentCaptor<Version> captor = ArgumentCaptor.forClass(Version.class);

    private MqiInitializer subject;

    @Before
    public void setUp() {
        subject = new MqiInitializer(versionRepository, databaseManager, serverService, ruleParamUtility);
        ReflectionTestUtils.setField(subject, "serverPort", "8080");
        ReflectionTestUtils.setField(subject, "serverType", "primary");
        ReflectionTestUtils.setField(subject, "serverVersion", "1.0.0");
    }

    @Test
    public void verifyMqiInitializerDoesWhatItsSupposedTo() throws ClassNotFoundException {
        subject.onApplicationEvent(contextRefreshedEvent);
        verify(ruleParamUtility, times(1)).saveRuleParams();
        when(versionRepository.findAll()).thenReturn(Arrays.asList(new Version("0.0.0")));
        verify(versionRepository, times(1)).save(captor.capture());
        assertThat(captor.getValue().getVersionId(), equalTo("0.0.0"));
    }

}
