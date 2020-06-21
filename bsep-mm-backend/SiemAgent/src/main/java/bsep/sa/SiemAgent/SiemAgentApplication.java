package bsep.sa.SiemAgent;

import io.krakens.grok.api.Grok;
import io.krakens.grok.api.GrokCompiler;
import io.krakens.grok.api.Match;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.Map;

@SpringBootApplication
@EnableScheduling
public class SiemAgentApplication implements CommandLineRunner{

	public static void main(String[] args) {
		SpringApplication.run(SiemAgentApplication.class, args);
	}

	@Autowired
	private RestTemplate restTemplate;

	@Override
	public void run(String... args) throws Exception {
	}

	public void sslHandshakeWithSiemCenter() throws InterruptedException {
		while (true) {
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
			Thread.sleep(5000);
		}
	}

	public void testGrok() {
		GrokCompiler grokCompiler = GrokCompiler.newInstance();
		grokCompiler.registerDefaultPatterns();

		final Grok grok = grokCompiler.compile("" +
				"%{SYSLOGTIMESTAMP:timestamp} " +
				"%{USERNAME:sourceUser} " +
				"%{WORD:action}:" +
				"%{GREEDYDATA}1 incorrect password attempt" +
				"%{GREEDYDATA}COMMAND=%{PATH:command}");

		String log = "Jun 16 19:08:33 aes sudo:      aes : 1 incorrect password attempt ; TTY=pts/0 ; PWD=/var/log ; USER=root ; COMMAND=/bin/ls\n";

		Match gm = grok.match(log);

		final Map<String, Object> capture = gm.capture();

		for (String key : capture.keySet()) {
			System.out.println(key + " - " + capture.get(key));
		}
	}
}
