package bsep.sc.SiemCenter;

import bsep.sc.SiemCenter.dto.rules.RuleDTO;
import bsep.sc.SiemCenter.model.Log;
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
    @Test
    public void test() throws InterruptedException {
        String rule = "package rules;\n" +
                "\n" +
                "import bsep.sc.SiemCenter.model.Log;\n" +
                "import bsep.sc.SiemCenter.events.Alarm;\n" +
                "import bsep.sc.SiemCenter.events.AlarmType;\n" +
                "import java.util.Date;\n" +
                "\n" +
                "rule \"*ENTER_NAME*\"\n" +
                "    no-loop true\n" +
                "    enabled true\n" +
                "    timer(cron:0/1 * * * * ?)\n" +
                "    when\n" +
                "        $log1: Log($src: machineIp)\n" +
                "        $c: Number(intValue >= 1) from accumulate(\n" +
                "            $log2: Log(\n" +
                "            ) over window:time(1m),\n" +
                "            count($log2)\n" +
                "        )\n" +
                "    then\n" +
                "        System.out.println(\"Rule name\");\n" +
                "        System.out.println($c);\n" +
                "end\n";

        String ruleName = "testRule";


        kieSessionService.insertEvent(new Log());
        Thread.sleep(5000);

        ruleService.create(new RuleDTO(ruleName, rule));
        Thread.sleep(5000);

        //ruleService.remove("testRule");
    }

}
