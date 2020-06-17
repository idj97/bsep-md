package bsep.sc.SiemCenter.service;

import bsep.sc.SiemCenter.dto.RuleTemplate;
import bsep.sc.SiemCenter.events.LogEvent;
import bsep.sc.SiemCenter.util.KieSessionTemplate;
import org.drools.template.ObjectDataCompiler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class AlarmService {


    @Value("${drools.templates.path}")
    private String templatePath;


    public String createRule(RuleTemplate ruleTemplate, String templateName) {

        InputStream template = AlarmService.class.getResourceAsStream(templatePath + templateName + ".drt");

        if(template == null) {
            return "Invalid template";  // TODO: throw exception
        }

        // add rule templates to create rules
        List<RuleTemplate> ruleData = new ArrayList<>();
        ruleData.add(ruleTemplate);
        ObjectDataCompiler converter = new ObjectDataCompiler();
        String drl = converter.compile(ruleData, template);

        System.out.print(drl); // print out created rules
        KieSessionTemplate.createSessionFromDRL(drl);
        return drl;

    }


    public int insertLogEvent(LogEvent logEvent) {

        if(KieSessionTemplate.templateSession == null) {
            return -1; // TODO: throw new exception saying no rules were created
        }

        KieSessionTemplate.templateSession.insert(logEvent);

        // return number of alarms activated
        return KieSessionTemplate.templateSession.fireAllRules();
    }


}
