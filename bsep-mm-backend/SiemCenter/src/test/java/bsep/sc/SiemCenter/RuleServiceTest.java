package bsep.sc.SiemCenter;

import bsep.sc.SiemCenter.dto.rules.RuleDTO;
import bsep.sc.SiemCenter.model.Alarm;
import bsep.sc.SiemCenter.model.Log;
import bsep.sc.SiemCenter.repository.AlarmRepository;
import bsep.sc.SiemCenter.repository.LogRepository;
import bsep.sc.SiemCenter.service.AlarmService;
import bsep.sc.SiemCenter.service.DateService;
import bsep.sc.SiemCenter.service.RuleService;
import bsep.sc.SiemCenter.service.drools.KieSessionService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RuleServiceTest {

    @Autowired
    private RuleService ruleService;

    @Autowired
    private KieSessionService kieSessionService;

    @Autowired
    private AlarmRepository alarmRepository;

    @Autowired
    private DateService dateService;

    @Autowired
    private AlarmService alarmService;

    @Test
    public void test() throws InterruptedException {
        String rule = "package rules;\n" +
                "\n" +
                "import bsep.sc.SiemCenter.model.Log;\n" +
                "import bsep.sc.SiemCenter.model.Alarm;\n" +
                "import java.util.Date;\n" +
                "import java.util.List;\n" +
                "\n" +
                "global bsep.sc.SiemCenter.service.AlarmService alarmService\n" +
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
                "        alarmService.add(new Alarm(\n" +
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

        logRepository.saveAll(Arrays.asList(log1, log2, log3, log4, log5, log6));

        ruleService.create(new RuleDTO(ruleName, rule));
        Thread.sleep(1000);
        kieSessionService.getKieSession().setGlobal("alarmRepository", alarmRepository);
        kieSessionService.getKieSession().setGlobal("alarmService", alarmService);

        kieSessionService.insertEvent(log1);
        kieSessionService.insertEvent(log2);
        kieSessionService.insertEvent(log3);
        kieSessionService.insertEvent(log4);
        kieSessionService.insertEvent(log5);
        kieSessionService.insertEvent(log6);

        Thread.sleep(10000);

        //ruleService.remove("Test cep rule");
    }

    @Test
    public void testSearch() {
        Page<Alarm> alarmPage = alarmRepository.search(
                dateService.getMinDate(),
                dateService.getMaxDate(),
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                PageRequest.of(0, 10)
        );

        System.out.println(alarmPage.getContent().size());
        Assert.assertEquals(6, alarmPage.getTotalElements());
    }

    @Autowired
    private LogRepository logRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    public void testLogSearch() {
        List<Log> logs = logRepository.findByMachineIp("1");
        Assert.assertEquals(3, logs.size());

        List<Alarm> alarms = mongoTemplate.find(
                query(where("logs").all(logs)
                        .and("name").is("alarm name")
                        .and("description").is("alarm description")
                        .and("machineIp").is("1")
                        .and("machineName").is("John")
                        .and("machineOS").is("Linux")
                        .and("alarmType").is("alarmType")
                        .and("agentInfo").is("Agent 1")
                ), Alarm.class);
        System.out.println(alarms.size());
    }
}
