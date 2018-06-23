package io.egia.mqi;

import io.egia.mqi.measure.RuleParam;
import io.egia.mqi.measure.RuleParamRepository;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class RuleParamUtility {

    private String packagePrefix = "io.egia.mqi.rules";

    private Reflections reflections = new Reflections(packagePrefix
            , new SubTypesScanner(false)
            , new TypeAnnotationsScanner());

    private Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(Rule.class);

    private RuleParamRepository ruleParamRepository;

    public RuleParamUtility(RuleParamRepository ruleParamRepository) {
        this.ruleParamRepository = ruleParamRepository;
    }

    public void saveRuleParams() throws ClassNotFoundException {
        ruleParamRepository.deleteAll();

        Class<?> ruleClass;
        int displayOrder = 1;
        for (Class c : annotated) {
            ruleClass = Class.forName(c.getName());
            Rule rule = ruleClass.getAnnotation(Rule.class);

            String ruleName = c.getName().substring(packagePrefix.length()+1);

            if (rule.params().length == 0) {
                ruleParamRepository.save(new RuleParam(ruleName, null, null, displayOrder));
                continue;
            }

            for (Param param : rule.params()) {
                ruleParamRepository.save(new RuleParam(ruleName,param.name(), param.type(), displayOrder));
                displayOrder++;
            }

            displayOrder = 1;
        }

    }
}