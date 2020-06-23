package bsep.sa.SiemAgent.service;

import bsep.sa.SiemAgent.model.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

@Component
public class LogSenderScheduler {

    @Autowired
    private LogService logService;

    @Value("${scheduling.mode}")
    private String schedulerMode;

    @Autowired
    private RestTemplate restTemplate;
    private ConcurrentLinkedQueue<Log> logs = new ConcurrentLinkedQueue<>();


    public void addLog(Log log) {
        logService.setMachineInfoToLog(log);
        logService.signLog(log);
        System.out.println(log);
        logs.add(log);
        realTimeSendLog();
    }

    @Scheduled(cron = "${scheduling.batch.cron}")
    public void batchSendLog() {
        if (schedulerMode.equals("batch")) {
            // get all logs added until this moment
            int size = logs.size();
            List<Log> logsToSend = new LinkedList<>();
            for (int i = 0; i < size; i++) {
                logsToSend.add(logs.poll());
            }

            if (logsToSend.size() > 0) {
                send(logsToSend);
            }
        }
    }

    public void realTimeSendLog() {
        if (schedulerMode.equals("real-time")) {
            Log log = logs.poll();
            List<Log> logsToSend = new LinkedList<>();
            logsToSend.add(log);
            send(logsToSend);
        }
    }

    public void send(List<Log> logs) {
        restTemplate.exchange(
                "https://localhost:8442/agents/api/logs",
                HttpMethod.POST,
                new HttpEntity(logs),
                String.class);
    }
}
