package bsep.sc.SiemCenter.util;

import org.kie.api.builder.Message;
import org.kie.api.builder.Results;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.utils.KieHelper;

import java.util.List;

public class KieSessionTemplate {

    public static KieSession templateSession = null;

    public static KieSession createSessionFromDRL(String drl) {
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

        templateSession =  kieHelper.build().newKieSession();
        return templateSession;
    }

    private KieSessionTemplate() { }


}
