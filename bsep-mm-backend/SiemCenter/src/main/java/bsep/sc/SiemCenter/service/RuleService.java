package bsep.sc.SiemCenter.service;

import bsep.sc.SiemCenter.dto.RuleTemplate;
import bsep.sc.SiemCenter.dto.rules.RuleDTO;
import bsep.sc.SiemCenter.events.LogEvent;
import bsep.sc.SiemCenter.exception.ApiBadRequestException;
import bsep.sc.SiemCenter.model.Rule;
import bsep.sc.SiemCenter.repository.RuleRepository;
import bsep.sc.SiemCenter.service.drools.KieSessionService;
import org.drools.template.ObjectDataCompiler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RuleService {

    @Autowired
    private KieSessionService kieSessionService;

    @Autowired
    private RuleRepository ruleRepository;

    @Value("${drools.templates.path}")
    private String templatePath;

    @Value("${kjar.rule.path}")
    private String kjarRulesPath;

    public void create(RuleDTO ruleDTO) {
        Optional<Rule> optionalRule = ruleRepository.findByRuleName(ruleDTO.getRuleName());
        if (!optionalRule.isPresent()) {
            String ruleContent = ruleDTO.getRuleContent().replace("@{ruleName}", ruleDTO.getRuleName());
            String rulePath = kjarRulesPath + ruleDTO.getRuleName() + ".drl";

            Rule rule = new Rule(ruleContent, ruleDTO.getRuleName());
            kieSessionService.addRule(ruleDTO.getRuleContent(), rulePath);
            ruleRepository.save(rule);
          } else {
            throw new ApiBadRequestException("Rule name is already in use.");
        }
    }

    public void remove(String ruleName) {
        Optional<Rule> optionalRule = ruleRepository.findByRuleName(ruleName);
        if (optionalRule.isPresent()) {
            String rulePath = kjarRulesPath + ruleName + ".drl";
            kieSessionService.removeRule(rulePath);
            ruleRepository.delete(optionalRule.get());
        } else {
            throw new ApiBadRequestException("Rule not found.");
        }
    }

    public String createRule(RuleTemplate ruleTemplate, String templateName) {
        InputStream template = RuleService.class.getResourceAsStream(templatePath + templateName + ".drt");

        if(template == null) {
            throw new ApiBadRequestException("Invalid template name");
        }

        if(ruleRepository.findByRuleName(ruleTemplate.getRuleName()).isPresent()) {
            throw new ApiBadRequestException("Rule name already exists");
        }

//         add rule templates to create rules
        List<RuleTemplate> ruleData = new ArrayList<>();
        ruleData.add(ruleTemplate);

        ObjectDataCompiler converter = new ObjectDataCompiler();
        String drl = converter.compile(ruleData, template);

        String combinedDrl = combinePreviousRules(drl); // add previous rules to drl
        ruleRepository.save(new Rule(drl, ruleTemplate.getRuleName())); // save new rule

        System.out.print(combinedDrl); // print out combined rules
//        kieSessionService.addRule(combinedDrl);
        return drl;
    }

    public String combinePreviousRules(String newDrl) {
        StringBuilder sb = new StringBuilder();
        sb.append(newDrl); // append first rule with createSessionFromDRLimports
        for(Rule rule: ruleRepository.findAll()) {
            // skip imports and append only rule content
            sb.append(rule.getRuleContent().substring(rule.getRuleContent().indexOf("rule")));
        }

        return sb.toString();
    }

    public int insertLogEvent(LogEvent logEvent) {
//        if(kieSessionService.getKieSession() == null) {
//            throw new ApiNotFoundException("No rules have been found");
//        }

//        kieSessionService.getKieSession().insert(logEvent);
//        return kieSessionService.getKieSession().fireAllRules(); // return number of alarms activated
        return 0;
    }
}
