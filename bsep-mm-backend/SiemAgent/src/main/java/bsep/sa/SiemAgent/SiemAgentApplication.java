package bsep.sa.SiemAgent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class SiemAgentApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(SiemAgentApplication.class, args);
	}

	@Autowired
	private RestTemplate restTemplate;

	@Override
	public void run(String... args) throws Exception {
		while (true) {
			try {
				ResponseEntity<String> responseEntity = restTemplate.exchange(
								"https://localhost:8442/agents/api/test",
								HttpMethod.GET,
								null,
								String.class);

				System.out.println(responseEntity.getStatusCode());
			} catch (ResourceAccessException ex) {
				ex.getMessage();
				System.out.println(ex.getLocalizedMessage());
			}
			Thread.sleep(5000);
		}
	}
}
