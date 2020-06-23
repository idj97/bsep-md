package bsep.sc.SiemCenter;

import bsep.sc.SiemCenter.dto.logs.LogSearchDTO;
import bsep.sc.SiemCenter.repository.LogRepository;
import bsep.sc.SiemCenter.service.DateService;
import bsep.sc.SiemCenter.service.LogService;
import org.drools.compiler.lang.DrlDumper;
import org.drools.compiler.lang.api.CEDescrBuilder;
import org.drools.compiler.lang.api.DescrBuilder;
import org.drools.compiler.lang.api.DescrFactory;
import org.drools.compiler.lang.api.RuleDescrBuilder;
import org.drools.compiler.lang.descr.PackageDescr;
import org.drools.compiler.lang.descr.RuleDescr;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MongoRepoTest {

    @Autowired
    private LogRepository logRepository;

    @Autowired
    private LogService logService;

    @Autowired
    private DateService dateService;

    @Test
    public void testSeachAll() {
        LogSearchDTO logSearchDTO = new LogSearchDTO();
        logSearchDTO.setPageNum(0);
        logSearchDTO.setPageSize(10);
        logSearchDTO.setMachineName("aes");

        Assert.assertEquals(5, logService.searchLogs(logSearchDTO).getItems().size());
    }

    @Test
    public void testSeachMessageRegex() {
        LogSearchDTO logSearchDTO = new LogSearchDTO();
        logSearchDTO.setPageNum(0);
        logSearchDTO.setPageSize(10);
        logSearchDTO.setMachineName("aes");
        logSearchDTO.setRawText("21:14");

        Assert.assertEquals(1, logService.searchLogs(logSearchDTO).getItems().size());
    }

    @Test
    public void testSeachMessageDates() {
        LogSearchDTO logSearchDTO = new LogSearchDTO();
        logSearchDTO.setPageNum(0);
        logSearchDTO.setPageSize(10);
        logSearchDTO.setMachineName("aes");
        logSearchDTO.setTimezone("CEST");
        logSearchDTO.setLowerGenericTimestamp("22.06.2020 20:55:00");
        logSearchDTO.setUpperGenericTimestamp("22.06.2020 21:15:00");

        Assert.assertEquals(3, logService.searchLogs(logSearchDTO).getItems().size());
    }

    @Test
    public void createRule() {
        PackageDescr pkg = DescrFactory.newPackage()
                .name("pkgName")
//                .newImport()
                .getDescr();

        PackageDescr desc = DescrFactory.newPackage().newGlobal().type("Service").identifier("svc").end().name("drools.rules")
                .newRule().name("Log rule")
                .lhs()

                .pattern("Log")
                .id("$log", false)
                .bind("$src", "machineIp", false)
                .constraint("$src == 192.168.0.1")
                .constraint("$ssda == asdasda")
                .end()

                .pattern("Log2")
                .id("$log2", false)
                .bind("$src2", "machineIp", false)
                .constraint("$src2 == 192.168.0.1")
                .constraint("$ssda2 == asdasda")
                .end()

                .pattern("Number").constraint("intValue > 5").from()
                    .accumulate()
                        .source()
                            .pattern("Asdsa").id("$log2", false).constraint("machineName == nme").end()
                        .end()
                        .function("count", null, false, "$log2")
                    .end()
                .end()
                .end()
                .rhs("sdasd").end().getDescr();

//        desc
//                .pattern("Log")
//                .id("$log", false)
//                .bind("$src", "machineIp", false)
//                .constraint("$src == 192.168.0.1")
//                .constraint("$ssda == asdasda");
//
//        desc = (CEDescrBuilder) desc
//                .pattern("Log2")
//                .id("$log1", false)
//                .bind("$src1", "machineIp", false)
//                .constraint("$src1 == 192.168.0.1")
//                .constraint("$ssda1 == asdasda").end();
//
//        RuleDescrBuilder ruleDesc = (RuleDescrBuilder)


//        RuleDescrBuilder ruleDesc2 = ruleDesc.
//                .pattern("Log")
//                .id("$log", false)
//                .bind("$src", "machineIp", false)
//                .constraint("$src == 192.168.0.1").end().end();
                //PackageDescr desc3 = ruleDesc.rhs("asjdakjsd").end().getDescr();
        DrlDumper dumper=new DrlDumper();
        String drl=dumper.dump(desc);



        System.out.print(drl);

        Assert.assertEquals(true, true);
    }
}
