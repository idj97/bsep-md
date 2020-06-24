package bsep.sc.SiemCenter.util;

import bsep.sc.SiemCenter.model.Rule;
import bsep.sc.SiemCenter.repository.RuleRepository;
import bsep.sc.SiemCenter.service.drools.KieSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Startup {

    @Autowired
    private RuleRepository ruleRepository;

    @Autowired
    private KieSessionService kieSessionService;

    @EventListener(ApplicationReadyEvent.class)
    private void createSessionWithOldRules() {

        List<Rule> rules = ruleRepository.findAll();

        if(rules.size() > 0) {
            StringBuilder sb = new StringBuilder();
            boolean firstRule = true;

            for(Rule rule: rules) {
                if(firstRule) {
                    sb.append(rule.getRuleContent()); // rule with imports
                } else {
                    sb.append(rule.getRuleContent().substring(rule.getRuleContent().indexOf("rule"))); // no imports
                }
                firstRule = false;
            }

//            kieSessionService.addRule(sb.toString());
        }

    }
}
