package bsep.sa.SiemAgent.service;

import bsep.sa.SiemAgent.model.Log;
import bsep.sa.SiemAgent.model.LogSource;
import bsep.sa.SiemAgent.model.LogPattern;
import bsep.sa.SiemAgent.readers.FileLogReader;
import bsep.sa.SiemAgent.readers.WindowsLogReader;
import bsep.sa.SiemAgent.util.ConfigurationUtil;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.LinkedList;
import java.util.List;

@Service
public class LogService {

    @Autowired
    private ConfigurationUtil configurationUtil;

    @Autowired
    private LogSenderScheduler logSenderScheduler;
    private String os = System.getProperty("os.name");

    @Value("${agent.info}")
    private String agentInfo;

    @PostConstruct
    public void startReaders() throws Exception {
        List<LogSource> logSources = getLogSources();

        for (LogSource logSource : logSources) {
            if (logSource.getType().equals("file")) {
                FileLogReader logReader = new FileLogReader(logSource, logSenderScheduler);
                new Thread(logReader).start();
            } else if (logSource.equals("windows-log")) {
                WindowsLogReader wlogReader = new WindowsLogReader(logSource, logSenderScheduler);
                new Thread(wlogReader).start();
            }
        }
    }

    public List<LogSource> getLogSources() throws Exception {
        JSONObject conf = configurationUtil.getConfiguration();
        JSONObject sourceConf = null;

        if (os.equals("Linux")) {
            sourceConf = (JSONObject) conf.get("linux");
        }  else if (os.toLowerCase().contains("win")) {
            sourceConf = (JSONObject) conf.get("win");
        } else {
            throw new Exception("Bad configuration.");
        }

        List<LogSource> logSources = new LinkedList<>();
        JSONArray logSourcesJson = (JSONArray) sourceConf.get("sources");
        for (int i = 0; i < logSourcesJson.size(); i++) {
            LogSource logSource = new LogSource();
            JSONObject logSourceJson = (JSONObject) logSourcesJson.get(i);

            logSource.setType((String) logSourceJson.get("type"));
            logSource.setSource((String) logSourceJson.get("source"));
            logSource.setReadFrequency((Long) logSourceJson.get("readFrequency"));

            JSONArray patterns = (JSONArray) logSourceJson.get("patterns");
            for (int j = 0; j < patterns.size(); j++) {
                LogPattern logPattern = new LogPattern();
                JSONObject pattern = (JSONObject) patterns.get(j);
                logPattern.setName((String) pattern.get("name"));
                logPattern.setType((String) pattern.get("type"));
                logPattern.setPattern((String) pattern.get("pattern"));
                logSource.getLogPatterns().add(logPattern);
            }
            logSources.add(logSource);
        }
        return logSources;
    }

    public void setMachineInfoToLog(Log log) {
        log.setMachineOS(os);
        log.setMachineIp(configurationUtil.getPublicIp());
        log.setAgentInfo(agentInfo);
    }
}
