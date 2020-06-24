package bsep.sc.SiemCenter.util;

import bsep.sc.SiemCenter.exception.ApiRuleInvalidException;
import bsep.sc.SiemCenter.model.Rule;
import bsep.sc.SiemCenter.repository.RuleRepository;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.KieServices;
import org.kie.api.builder.Message;
import org.kie.api.builder.Results;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.utils.KieHelper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class KieSessionTemplate {

    @Autowired
    private KieBaseConfiguration kieBaseConfiguration;

    private KieSession templateSession;

    public void createSessionFromDRL(String drl) {
        KieHelper kieHelper = new KieHelper();

        kieHelper.addContent(drl, ResourceType.DRL);

        Results results = kieHelper.verify();

        if (results.hasMessages(Message.Level.WARNING, Message.Level.ERROR)){

            List<Message> messages = results.getMessages(Message.Level.WARNING, Message.Level.ERROR);
            StringBuilder sb = new StringBuilder();
            sb.append("<p> Rule creation failed. </p>");

            for (Message message : messages) {
                sb.append("<p>");
                sb.append(message.getText());
                sb.append("</p>");
            }

            throw new ApiRuleInvalidException(sb.toString());
        }

        if(templateSession != null) {
            templateSession.dispose();
        }

        templateSession = kieHelper.build(kieBaseConfiguration).newKieSession();
    }



    public KieSession getTemplateSession() {
        return this.templateSession;
    }

}
