package bsep.sa.SiemAgent.config;

import bsep.sa.SiemAgent.util.KeyStoreUtil;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

@Configuration
public class RestTemplateConfig {
    @Autowired
    private ResourceLoader resourceLoader;
    private String password = "";

    @Bean
    public RestTemplate getRestTemplate() throws IOException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException {

        TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> {
            SSLTrustManager customSSLTrustManager = new SSLTrustManager();
            try { customSSLTrustManager.checkServerTrusted(chain, ""); }
            catch (Exception ex) { return false; }
            return true;
        };

        String keyStorePath = resourceLoader.getResource("classpath:keystore.jks").getFile().getAbsolutePath();

        SSLContextBuilder builder = SSLContexts.custom();


        SSLContext sslContext = SSLContextBuilder
                .create()
                .loadKeyMaterial(KeyStoreUtil.getKeyStore(keyStorePath, password), password.toCharArray())
                .loadTrustMaterial(null, acceptingTrustStrategy)
                .build();

        CloseableHttpClient httpClient = HttpClients
                .custom()
                .setSslcontext(sslContext)
                .build();

        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setHttpClient(httpClient);
        return new RestTemplate(requestFactory);
    }
}
