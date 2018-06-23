package io.egia.mqi;

import io.egia.mqi.measure.RuleParam;
import io.egia.mqi.measure.RuleParamRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest(classes = {StandaloneConfig.class})
public class RuleParamUtilityTest {

    private RuleParam[] expected = {
            new RuleParam("AgeWithinDateRange","AGE","INTEGER", 1)
            , new RuleParam("AgeWithinDateRange","START_DATE","DATE",2)
            , new RuleParam("AgeWithinDateRange","END_DATE","DATE",3)
            , new RuleParam("ExitMeasure","CONTINUE","BOOLEAN",1)
            , new RuleParam("SetResult",null,null,1)
    };

    @Mock
    private RuleParamRepository ruleParamRepository;

    @Captor
    private ArgumentCaptor<RuleParam> captor = ArgumentCaptor.forClass(RuleParam.class);

    private String packagePrefix = "io.egia.mqi.rules";

    private Reflections reflections = new Reflections(packagePrefix
            , new SubTypesScanner(false)
            , new TypeAnnotationsScanner());

    @Test
    public void rulesInsertTheirMetaDataInMqiDb() throws ClassNotFoundException {
        RuleParamUtility subject = new RuleParamUtility(ruleParamRepository);
        subject.saveRuleParams();
        verify(ruleParamRepository, times(expected.length)).save(captor.capture());
        assertThat(captor.getAllValues()).contains(expected);
    }

    @Test
    public void allClassesInTheRulesPackageAreAreAnnotated() {
        Set<String> allClasses = reflections.getAllTypes();
        String[] expected = allClasses.stream().map(String::toString).toArray(String[]::new);
        Set<String> allAnnotatedClasses = reflections.getTypesAnnotatedWith(Rule.class).stream()
                .map(Class::getName).collect(Collectors.toSet());
        assertThat(allAnnotatedClasses).contains(expected);
    }
}
