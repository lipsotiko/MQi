package io.egia.mqi;

import io.egia.mqi.measure.RuleParamUtility;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.event.ContextRefreshedEvent;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class MqiInitializerTest {

    @Mock private RuleParamUtility ruleParamUtility;
    @Mock private ContextRefreshedEvent contextRefreshedEvent;

    @Test
    public void save_rule_params_is_called() throws ClassNotFoundException {
        MqiInitializer subject = new MqiInitializer(ruleParamUtility);
        subject.onApplicationEvent(contextRefreshedEvent);
        verify(ruleParamUtility, times(1)).saveRuleParams();
    }

}
