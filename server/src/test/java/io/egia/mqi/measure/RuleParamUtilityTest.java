package io.egia.mqi.measure;

import io.egia.mqi.RuleTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;

import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class RuleParamUtilityTest {

    private RuleParam[] expected = {
            new RuleParam("AgeWithinDateRange","FROM_AGE","INTEGER")
            , new RuleParam("AgeWithinDateRange","TO_AGE","INTEGER")
            , new RuleParam("AgeWithinDateRange","START_DATE","DATE")
            , new RuleParam("AgeWithinDateRange","END_DATE","DATE")
            , new RuleParam("ExitMeasure","","INVISIBLE")
            , new RuleParam("SetResultCode","RESULT_CODE","TEXT")
            , new RuleParam("HasCodeSet","CODE_SET","TEXT")
    };

    @Mock private RuleParamRepo ruleParamRepo;
    @Captor private ArgumentCaptor<RuleParam> captor = ArgumentCaptor.forClass(RuleParam.class);
    private String packagePrefix = "io.egia.mqi.measure.rules";

    private Reflections reflections = new Reflections(packagePrefix
            , new SubTypesScanner(false)
            , new TypeAnnotationsScanner());

    @Test
    public void rulesInsertTheirMetaDataInMqiDb() throws ClassNotFoundException {
        RuleParamUtility subject = new RuleParamUtility(ruleParamRepo);
        subject.saveRuleParams();
        verify(ruleParamRepo, times(expected.length)).save(captor.capture());
        assertThat(captor.getAllValues()).contains(expected);
    }

    @Test
    public void allClassesInTheRulesPackageAreAreAnnotated() {
        Set<String> allClasses = reflections.getAllTypes();
        Set<String> allTestClasses = reflections.getTypesAnnotatedWith(RuleTest.class).stream()
                .map(Class::getName).collect(Collectors.toSet());

        Predicate<String> testClassFilter = allTestClasses::contains;
        allClasses.removeIf(testClassFilter);

        String[] expected = allClasses.stream().map(String::toString).toArray(String[]::new);
        Set<String> allAnnotatedClasses = reflections.getTypesAnnotatedWith(RuleParams.class).stream()
                .map(Class::getName).collect(Collectors.toSet());
        assertThat(allAnnotatedClasses).contains(expected);
    }
}
