package bsep.sa.SiemAgent.service;

import bsep.sa.SiemAgent.model.Log;
import bsep.sa.SiemAgent.model.LogSource;
import bsep.sa.SiemAgent.model.LogPattern;
import bsep.sa.SiemAgent.readers.FileLogReader;
import bsep.sa.SiemAgent.readers.WindowsLogReader;
import bsep.sa.SiemAgent.util.ConfigurationUtil;
import bsep.sa.SiemAgent.util.KeyStoreUtil;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.security.PrivateKey;
import java.security.Signature;
import java.util.Base64;
import java.util.LinkedList;
import java.util.List;

@Service
public class LogService {

    @Autowired
    private ConfigurationUtil configurationUtil;

    @Autowired
    private LogSenderScheduler logSenderScheduler;
    private String os = System.getProperty("os.name");

    @Autowired
    private ResourceLoader resourceLoader;

    @Value("${agent.info}")
    private String agentInfo;

    @Value("${signing.algorithm}")
    private String signingAlgorithm;

    @PostConstruct
    public void startReaders() throws Exception {
        List<LogSource> logSources = getLogSources();

        for (LogSource logSource : logSources) {
            if (logSource.getType().equals("file")) {
                FileLogReader logReader = new FileLogReader(logSource, logSenderScheduler);
                new Thread(logReader).start();
            } else if (logSource.getType().equals("windows-log")) {
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

    public void signLog(Log log) {
        try {
            String keystorePath = resourceLoader.getResource("classpath:keystore.jks").getURL().getPath();
            PrivateKey privateKey = (PrivateKey) KeyStoreUtil.getKey(keystorePath, "ssl-client", "");

            Signature sig = Signature.getInstance("SHA1withRSA");
            sig.initSign(privateKey);
            sig.update(log.toString().getBytes());
            String signature = Base64.getEncoder().encodeToString(sig.sign());
            log.setSigningAlgorithm(signingAlgorithm);
            log.setSignature(signature);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
