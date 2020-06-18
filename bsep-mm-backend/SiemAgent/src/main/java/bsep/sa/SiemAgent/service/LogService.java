package bsep.sa.SiemAgent.service;

import bsep.sa.SiemAgent.model.Log;
import bsep.sa.SiemAgent.model.LogFile;
import bsep.sa.SiemAgent.model.LogPattern;
import bsep.sa.SiemAgent.readers.LinuxLogReader;
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
        List<LogFile> logFiles = getLogFiles();
        String publicIp = configurationUtil.getPublicIp();
        if (os.equals("Linux")) {
            for (LogFile logFile : logFiles) {
                LinuxLogReader logReader = new LinuxLogReader(logFile, logSenderScheduler);
                new Thread(logReader).start();
            }
        }

        else if (os.toLowerCase().contains("win")) {
            for (LogFile logfile: logFiles) {
                WindowsLogReader wlogReader = new WindowsLogReader(logfile, logSenderScheduler);
                new Thread(wlogReader).start();
            }
        }
    }

    public List<LogFile> getLogFiles() throws Exception {
        JSONObject conf = configurationUtil.getConfiguration();
        List<LogFile> logFiles = null;

        if (os.equals("Linux")) {
            JSONObject linuxConf = (JSONObject) conf.get("linux");

            logFiles = new LinkedList<>();
            JSONArray files = (JSONArray) linuxConf.get("files");
            for (int i = 0; i < files.size(); i++) {
                LogFile logFile = new LogFile();
                JSONObject file = (JSONObject) files.get(i);
                logFile.setPath((String) file.get("path"));
                logFile.setReadFrequency((Long) file.get("readFrequency"));

                JSONArray patterns = (JSONArray) file.get("patterns");
                for (int j = 0; j < patterns.size(); j++) {
                    LogPattern logPattern = new LogPattern();
                    JSONObject pattern = (JSONObject) patterns.get(j);
                    logPattern.setName((String) pattern.get("name"));
                    logPattern.setType((String) pattern.get("type"));
                    logPattern.setPattern((String) pattern.get("pattern"));
                    logFile.getLogPatterns().add(logPattern);
                }
                logFiles.add(logFile);
            }
        }
        else if (os.toLowerCase().contains("win")) {
            JSONObject winConf = (JSONObject) conf.get("win");

            logFiles = new LinkedList<>();
            JSONArray files = (JSONArray) winConf.get("files");
            for (int i = 0; i < files.size(); i++) {
                LogFile logFile = new LogFile();
                JSONObject file = (JSONObject) files.get(i);
                logFile.setPath((String) file.get("path"));
                logFile.setReadFrequency((Long) file.get("readFrequency"));

                JSONArray patterns = (JSONArray) file.get("patterns");
                for (int j = 0; j < patterns.size(); j++) {
                    LogPattern logPattern = new LogPattern();
                    JSONObject pattern = (JSONObject) patterns.get(j);
                    logPattern.setName((String) pattern.get("name"));
                    logPattern.setType((String) pattern.get("type"));
                    logPattern.setPattern((String) pattern.get("pattern"));
                    logFile.getLogPatterns().add(logPattern);
                }
                logFiles.add(logFile);
            }
        }


        return logFiles;
    }

    public void setMachineInfoToLog(Log log) {
        log.setMachineOS(os);
        log.setMachineIp(configurationUtil.getPublicIp());
        log.setAgentInfo(agentInfo);
    }
}
