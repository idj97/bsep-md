package bsep.sc.SiemCenter.service;

import bsep.sc.SiemCenter.dto.RuleTemplate;
import bsep.sc.SiemCenter.events.LogEvent;
import bsep.sc.SiemCenter.exception.ApiBadRequestException;
import bsep.sc.SiemCenter.exception.ApiNotFoundException;
import bsep.sc.SiemCenter.model.Rule;
import bsep.sc.SiemCenter.repository.RuleRepository;
import bsep.sc.SiemCenter.util.KieSessionTemplate;
import org.drools.template.ObjectDataCompiler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class RuleService {

    @Autowired
    private KieSessionTemplate kieSessionTemplate;

    @Autowired
    private RuleRepository ruleRepository;

    @Value("${drools.templates.path}")
    private String templatePath;


    public String createRule(RuleTemplate ruleTemplate, String templateName) {
        InputStream template = RuleService.class.getResourceAsStream(templatePath + templateName + ".drt");

        if(template == null) {
            throw new ApiBadRequestException("Invalid template name");
        }

        if(ruleRepository.findByRuleName(ruleTemplate.getRuleName()).isPresent()) {
            throw new ApiBadRequestException("Rule name already exists");
        }

        // add rule templates to create rules
        List<RuleTemplate> ruleData = new ArrayList<>();
        ruleData.add(ruleTemplate);

        ObjectDataCompiler converter = new ObjectDataCompiler();
        String drl = converter.compile(ruleData, template);

        String combinedDrl = combinePreviousRules(drl); // add previous rules to drl
        ruleRepository.save(new Rule(drl, ruleTemplate.getRuleName())); // save new rule

        System.out.print(combinedDrl); // print out combined rules
        kieSessionTemplate.createSessionFromDRL(combinedDrl);
        return drl;
    }


    public String combinePreviousRules(String newDrl) {
        StringBuilder sb = new StringBuilder();
        sb.append(newDrl); // append first rule with imports
        for(Rule rule: ruleRepository.findAll()) {
            // skip imports and append only rule content
            sb.append(rule.getRuleContent().substring(rule.getRuleContent().indexOf("rule")));
        }

        return sb.toString();
    }

    public int insertLogEvent(LogEvent logEvent) {
        if(kieSessionTemplate.getTemplateSession() == null) {
            throw new ApiNotFoundException("No rules have been found");
        }

        kieSessionTemplate.getTemplateSession().insert(logEvent);
        return kieSessionTemplate.getTemplateSession().fireAllRules(); // return number of alarms activated
    }
}
