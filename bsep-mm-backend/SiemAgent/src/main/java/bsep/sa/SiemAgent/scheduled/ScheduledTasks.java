package bsep.sa.SiemAgent.scheduled;


import bsep.sa.SiemAgent.readers.WindowsLogReader;
import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Advapi32;
import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.Date;

@Component
public class ScheduledTasks {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${scheduling.simpleMessage.enabled}")
    private boolean simpleMessageEnabled;

    @Scheduled(fixedRate = 5000)
    public void ReadWindowsLogEvents() {
        /*Advapi32Util.EventLogIterator iter = new Advapi32Util.EventLogIterator("Security");
        while(iter.hasNext()) {
            Advapi32Util.EventLogRecord record = iter.next();
            long timestamp = record.getRecord().TimeGenerated.longValue() * 1000;
            Date d = new Date(timestamp);
            System.out.println(record.getRecordNumber()
                    + "Time Generated: " + d.toString()
                    + ": Event ID: " + record.getEventId()
                    + ", Event Type: " + record.getType()
                    + ", Event Source: " + record.getSource());
        }*/
        System.out.println(System.getProperty("os.name").toLowerCase().contains("win"));


        WindowsLogReader wlr = new WindowsLogReader();
        //Advapi32Util.EventLogRecord eventLogRecord = wlr.getLatestEvent("System");
        //System.out.println(eventLogRecord.getRecord().EventID.);
        System.out.println(new Date(wlr.getNewestEvent("System").TimeGenerated.longValue() * 1000));

    }

    @Scheduled(fixedRate = 5000)
    public void simpleMessage() {
        if (!simpleMessageEnabled) return;
        try {
            ResponseEntity<String> responseEntity = restTemplate.exchange(
                    "https://localhost:8442/agents/api/test",
                    HttpMethod.GET,
                    null,
                    String.class);

            System.out.println(responseEntity.getStatusCode() + " at " + new Date());
        } catch (ResourceAccessException ex) {
            System.out.println("Error, reason " + ex.getCause().getMessage());
        }
    }
}
