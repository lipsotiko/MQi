package io.egia.mqi;

import io.egia.mqi.measure.RuleParamUtility;
import io.egia.mqi.server.ServerService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class MqiInitializerTest {

    @Mock
    private ServerService serverService;
    @Mock
    private RuleParamUtility ruleParamUtility;
    @Mock
    private ContextRefreshedEvent contextRefreshedEvent;

    private MqiInitializer subject;

    @Before
    public void setUp() {
        subject = new MqiInitializer(serverService, ruleParamUtility);
        ReflectionTestUtils.setField(subject, "serverPort", "8080");
        ReflectionTestUtils.setField(subject, "serverType", "primary");
    }

    @Test
    public void verifyMqiInitializerDoesWhatItsSupposedTo() throws ClassNotFoundException {
        subject.onApplicationEvent(contextRefreshedEvent);
        verify(ruleParamUtility, times(1)).saveRuleParams();
    }

}
