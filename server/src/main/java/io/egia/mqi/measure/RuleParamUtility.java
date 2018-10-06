package io.egia.mqi.measure;

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

    private RuleParamRepo ruleParamRepo;

    public RuleParamUtility(RuleParamRepo ruleParamRepo) {
        this.ruleParamRepo = ruleParamRepo;
    }

    public void saveRuleParams() throws ClassNotFoundException {
        ruleParamRepo.deleteAll();
        Class<?> ruleClass;

        for (Class c : annotated) {
            ruleClass = Class.forName(c.getName());
            Rule rule = ruleClass.getAnnotation(Rule.class);
            String ruleName = c.getName().substring(packagePrefix.length()+1);

            if (rule.params().length == 0) {
                ruleParamRepo.save(new RuleParam(ruleName, null, null));
                continue;
            }

            for (Param param : rule.params()) {
                ruleParamRepo.save(new RuleParam(ruleName,param.name(), param.type()));
            }
        }
    }
}