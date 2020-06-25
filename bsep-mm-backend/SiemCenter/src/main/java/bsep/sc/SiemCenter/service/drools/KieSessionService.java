package bsep.sc.SiemCenter.service.drools;

import bsep.sc.SiemCenter.exception.ApiBadRequestException;
import bsep.sc.SiemCenter.exception.ApiRuleInvalidException;
import org.apache.maven.shared.invoker.*;
import org.kie.api.KieBase;
import org.kie.api.builder.Message;
import org.kie.api.builder.Results;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.utils.KieHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
public class KieSessionService {

    @Autowired
    private KieBase kieBase;
    private KieSession kieSession;

    @Value("${kjar.pom.path}")
    private String kjarPomPath;

    @PostConstruct
    public void startup() {
        kieSession = kieBase.newKieSession();
        startEngine();
        trackFacts();
    }

    public void insertEvent(Object object) {
        kieSession.insert(object);
    }

    public void startEngine() {
        new Thread(() -> {kieSession.fireUntilHalt();}).start();
    }

    public void stopEngine() {
        kieSession.halt();
    }

    public void updateKjar() {
        InvocationRequest request = new DefaultInvocationRequest();
        request.setDebug(false);
        request.setPomFile(new File(kjarPomPath));
        request.setGoals(Arrays.asList("clean", "install"));

        stopEngine();
        try {
            Invoker invoker = new DefaultInvoker();
            invoker.execute(request);
        } catch (MavenInvocationException e) {
            e.printStackTrace();
        }
        startEngine();
    }

    public void addRule(String rule, String rulePath) {
        checkRule(rule);
        saveOnDisk(rule, rulePath);
        updateKjar();
    }

    public void removeRule(String rulePath) {
        removeFromDisk(rulePath);
        updateKjar();
    }

    public void saveOnDisk(String rule, String rulePath) {
        try {
            FileWriter fileWriter = new FileWriter(rulePath);
            fileWriter.write(rule);
            fileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void removeFromDisk(String rulePath) {
        File file = new File(rulePath);
        if (!file.delete()) {
            throw new ApiBadRequestException("Failed to delete file");
        }
    }

    public void checkRule(String rule) {
        KieHelper kieHelper = new KieHelper();
        kieHelper.addContent(rule, ResourceType.DRL);
        Results results = kieHelper.verify();

        if (results.hasMessages(Message.Level.WARNING, Message.Level.ERROR)){
            List<Message> messages = results.getMessages(Message.Level.WARNING, Message.Level.ERROR);
            StringBuilder sb = new StringBuilder();
            sb.append("Rule validation failed. ");
            for (Message message : messages) {
                sb.append(message.getText());
                sb.append("; ");
            }
            throw new ApiRuleInvalidException(sb.toString());
        }
    }

    public void trackFacts() {
        new Thread(() -> {
            try {
                while (true) {
                    System.out.println(kieSession.getFactCount() + " " + new Date());
                    Thread.sleep(5000);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
