package bsep.sc.SiemCenter.service;

import bsep.sc.SiemCenter.dto.RuleTemplate;
import bsep.sc.SiemCenter.events.LogEvent;
import bsep.sc.SiemCenter.exception.ApiBadRequestException;
import bsep.sc.SiemCenter.exception.ApiNotFoundException;
import bsep.sc.SiemCenter.util.KieSessionTemplate;
import org.drools.template.ObjectDataCompiler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class AlarmService {

    @Autowired
    private KieSessionTemplate kieSessionTemplate;

    @Value("${drools.templates.path}")
    private String templatePath;


    public String createRule(RuleTemplate ruleTemplate, String templateName) {

        InputStream template = AlarmService.class.getResourceAsStream(templatePath + templateName + ".drt");

        if(template == null) {
            throw new ApiBadRequestException("Invalid template name");
        }

        // add rule templates to create rules
        List<RuleTemplate> ruleData = new ArrayList<>();
        ruleData.add(ruleTemplate);

        ObjectDataCompiler converter = new ObjectDataCompiler();
        String drl = converter.compile(ruleData, template);

        System.out.print(drl); // print out created rules
        kieSessionTemplate.createSessionFromDRL(drl);
        return drl;

    }


    public int insertLogEvent(LogEvent logEvent) {

        if(kieSessionTemplate.getTemplateSession() == null) {
            throw new ApiNotFoundException("No rules have been found");
        }

        kieSessionTemplate.getTemplateSession().insert(logEvent);
        return kieSessionTemplate.getTemplateSession().fireAllRules(); // return number of alarms activated
    }


}
