package io.egia.mqi;

import io.egia.mqi.measure.RuleParamUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
public class MqiInitializer implements ApplicationListener<ContextRefreshedEvent> {
    private Logger log = LoggerFactory.getLogger(MqiInitializer.class);
    private RuleParamUtility ruleParamUtility;

    @Value("${mqi.properties.system.version}")
    private String systemVersion;

    MqiInitializer(RuleParamUtility ruleParamUtility) {
        this.ruleParamUtility = ruleParamUtility;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        try {
            ruleParamUtility.saveRuleParams();
            log.info("--------------------------------------------------");
            log.info("							                        ");
            log.info("           '||    ||'  ..|''||   '||'             ");
            log.info("            |||  |||  .|'    ||   ||              ");
            log.info("            |'|..'||  ||      ||  ||              ");
            log.info("            | '|' ||  '|.  '. '|  ||              ");
            log.info("           .|. | .||.   '|...'|. .||.             ");
            log.info("							                        ");
            log.info("          Egia Software Solutions, Inc            ");
            log.info("          Medical Quality Informatics             ");
            log.info(String.format("          v%s", this.systemVersion));
            log.info("							                        ");
            log.info("--------------------------------------------------");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
