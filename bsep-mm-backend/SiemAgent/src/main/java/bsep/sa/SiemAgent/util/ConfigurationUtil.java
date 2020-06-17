package bsep.sa.SiemAgent.util;

import com.google.gson.Gson;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

@Service
public class ConfigurationUtil {

    @Autowired
    private ResourceLoader resourceLoader;
    private String publicIp; // cache

    public JSONObject getConfiguration() throws IOException, ParseException {
        JSONParser parser = new JSONParser();
        String confPath = resourceLoader.getResource("classpath:conf.json").getFile().getAbsolutePath();
        Object obj = parser.parse(new FileReader(confPath));
        JSONObject jsonObject = (JSONObject) obj;
        return jsonObject;
    }

    public String getPublicIp() {
        try {
            if (publicIp == null) {
                URL whatismyip = new URL("http://checkip.amazonaws.com");
                BufferedReader in = null;
                try {
                    in = new BufferedReader(new InputStreamReader(
                            whatismyip.openStream()));
                    String ip = in.readLine();
                    publicIp = ip;
                    return ip;
                } finally {
                    if (in != null) {
                        try {
                            in.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else {
                return publicIp;
            }
        } catch (Exception ex) {
            return "unknown";
        }

    }

}
