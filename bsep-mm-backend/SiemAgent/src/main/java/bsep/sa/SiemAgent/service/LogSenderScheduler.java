package bsep.sa.SiemAgent.service;

import bsep.sa.SiemAgent.model.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.ConcurrentLinkedQueue;

@Component
public class LogSenderScheduler {

    @Autowired
    private RestTemplate restTemplate;

    private ConcurrentLinkedQueue<Log> logs = new ConcurrentLinkedQueue<>();


    public void send() {

    }
}
