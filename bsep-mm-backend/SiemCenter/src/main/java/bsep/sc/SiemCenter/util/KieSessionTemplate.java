package bsep.sc.SiemCenter.util;

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
            for (Message message : messages) {
                System.out.println("Error: "+ message.getText());
            }

            throw new IllegalStateException("Compilation errors were found. Check the logs.");
        }

        templateSession =  kieHelper.build(kieBaseConfiguration).newKieSession();
    }



    public KieSession getTemplateSession() {
        return this.templateSession;
    }

}
