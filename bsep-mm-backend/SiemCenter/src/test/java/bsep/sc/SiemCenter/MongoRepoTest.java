package bsep.sc.SiemCenter;

import bsep.sc.SiemCenter.dto.logs.LogSearchDTO;
import bsep.sc.SiemCenter.repository.LogRepository;
import bsep.sc.SiemCenter.service.DateService;
import bsep.sc.SiemCenter.service.LogService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

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
}
