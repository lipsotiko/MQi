package io.egia.mqi;

import io.egia.mqi.measure.RuleParam;
import io.egia.mqi.measure.RuleParamRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest(classes = {StandaloneConfig.class})
public class RuleParamUtilityTest {

    private RuleParam[] expected = {
            new RuleParam("AgeWithinDateRange","AGE","INTEGER", 1)
            , new RuleParam("AgeWithinDateRange","START_DATE","DATE",2)
            , new RuleParam("AgeWithinDateRange","END_DATE","DATE",3)
            , new RuleParam("ExitMeasure","CONTINUE","BOOLEAN",1)
            , new RuleParam("SetResult","","",1)
    };

    @Mock
    private RuleParamRepository ruleParamRepository;

    @Captor
    private ArgumentCaptor<RuleParam> captor = ArgumentCaptor.forClass(RuleParam.class);

    @Test
    public void rulesInsertTheirMetaDataInMqiDb() throws ClassNotFoundException {
        RuleParamUtility subject = new RuleParamUtility(ruleParamRepository);
        subject.saveRuleParams();
        verify(ruleParamRepository, times(expected.length)).save(captor.capture());
        when(ruleParamRepository.findAll()).thenReturn(captor.getAllValues());
        assertThat(ruleParamRepository.findAll()).contains(expected);
    }
}
