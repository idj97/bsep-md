package bsep.sc.SiemCenter;

import bsep.sc.SiemCenter.dto.rules.RuleDTO;
import bsep.sc.SiemCenter.model.Log;
import bsep.sc.SiemCenter.repository.AlarmRepository;
import bsep.sc.SiemCenter.service.RuleService;
import bsep.sc.SiemCenter.service.drools.KieSessionService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RuleServiceTest {

    @Autowired
    private RuleService ruleService;

    @Autowired
    private KieSessionService kieSessionService;

    @Autowired
    private AlarmRepository alarmRepository;

    @Test
    public void test() throws InterruptedException {
        String rule = "package rules;\n" +
                "\n" +
                "import bsep.sc.SiemCenter.model.Log;\n" +
                "import bsep.sc.SiemCenter.model.Alarm;\n" +
                "import java.util.Date;\n" +
                "import java.util.List;\n" +
                "\n" +
                "global bsep.sc.SiemCenter.repository.AlarmRepository alarmRepository\n" +
                "\n" +
                "rule \"Test cep rule\"\n" +
                "    no-loop true\n" +
                "    enabled true\n" +
                "    timer(cron:0/5 * * * * ?)\n" +
                "    when\n" +
                "        $log: Log($src: machineIp) and\n" +
                "        $logs: List() from collect(Log(machineIp == $src) over window:time(5s)) and\n" +
                "        $num: Number(intValue >= 3) from accumulate(\n" +
                "            $log2: Log(machineIp == $src) over window:time(5s),\n" +
                "            count($log2))\n" +
                "    then\n" +
                "        alarmRepository.save(new Alarm(\n" +
                "                \"alarm name\",\n" +
                "                \"alarm description\",\n" +
                "                \"alarmType\",\n" +
                "                $log.getMachineIp(),\n" +
                "                $log.getMachineOS(),\n" +
                "                $log.getMachineName(),\n" +
                "                $log.getAgentInfo(),\n" +
                "                new Date(),\n" +
                "                $logs\n" +
                "        ));\n" +
                "end\n";

        String ruleName = "testRule";

        Log log1 = new Log();
        log1.setMachineIp("1");
        log1.setMachineOS("Linux");
        log1.setMachineName("John");
        log1.setAgentInfo("Agent 1");

        Log log2 = new Log();
        log2.setMachineIp("1");
        log2.setMachineOS("Linux");
        log2.setMachineName("John");
        log2.setAgentInfo("Agent 1");

        Log log3 = new Log();
        log3.setMachineIp("1");
        log3.setMachineOS("Linux");
        log3.setMachineName("John");
        log3.setAgentInfo("Agent 1");

        Log log4 = new Log();
        log4.setMachineIp("2");
        log4.setMachineOS("Windows");
        log4.setMachineName("Mike");
        log4.setAgentInfo("Agent 2");

        Log log5 = new Log();
        log5.setMachineIp("2");
        log5.setMachineOS("Windows");
        log5.setMachineName("Mike");
        log5.setAgentInfo("Agent 2");

        Log log6 = new Log();
        log6.setMachineIp("2");
        log6.setMachineOS("Windows");
        log6.setMachineName("Mike");
        log6.setAgentInfo("Agent 2");

        ruleService.create(new RuleDTO(ruleName, rule));
        Thread.sleep(1000);
        kieSessionService.getKieSession().setGlobal("alarmRepository", alarmRepository);

        kieSessionService.insertEvent(log1);
        kieSessionService.insertEvent(log2);
        kieSessionService.insertEvent(log3);
        kieSessionService.insertEvent(log4);
        kieSessionService.insertEvent(log5);
        kieSessionService.insertEvent(log6);

        Thread.sleep(10000);

        //ruleService.remove("Test cep rule");
    }

}
