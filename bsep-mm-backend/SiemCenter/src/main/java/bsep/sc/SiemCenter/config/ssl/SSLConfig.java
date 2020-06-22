package bsep.sc.SiemCenter.config.ssl;

import org.apache.catalina.connector.Connector;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.tomcat.util.net.SSLHostConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.io.IOException;

@Configuration
public class SSLConfig {

    @Autowired
    ResourceLoader resourceLoader;

    @Bean
    public ServletWebServerFactory servletContainer() throws IOException {
        Connector httpsX509AuthConnector = getHttpsConnectorWithX509Authentication();
        Connector httpsConnector = getHttpsConnector();

        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();
        tomcat.addAdditionalTomcatConnectors(httpsX509AuthConnector);
        tomcat.addAdditionalTomcatConnectors(httpsConnector);

        return tomcat;
    }

    public Connector getHttpsConnectorWithX509Authentication() throws IOException {
        String keyStorePath = resourceLoader.getResource("classpath:keystore.jks").getFile().getAbsolutePath();

        Connector connector = new Connector(TomcatServletWebServerFactory.DEFAULT_PROTOCOL);
        connector.setPort(8442);
        connector.setScheme("https");
        connector.setSecure(true);
        connector.setAttribute("SSLEnabled", "true");

        SSLHostConfig sslHostConfig = new SSLHostConfig();

        // VERIFICAATION AND REVOCATION CHECKING
        sslHostConfig.setCertificateVerification("required");
        sslHostConfig.setRevocationEnabled(true);
        sslHostConfig.setSslProtocol("TLS");

        // SERVER CERTIFICATE CONFIGURATION
        sslHostConfig.setCertificateKeystoreFile(keyStorePath);
        sslHostConfig.setCertificateKeystorePassword("");
        sslHostConfig.setCertificateKeyAlias("ssl-server");
        sslHostConfig.setCertificateKeyPassword("");

        // TRUST CONFIGURATION
        sslHostConfig.setTrustManagerClassName("bsep.sc.SiemCenter.config.ssl.SSLTrustManager");

        // ADDITIONAL
        sslHostConfig.setSslProtocol("TLS");
        sslHostConfig.setDisableSessionTickets(true);
        connector.addSslHostConfig(sslHostConfig);

        return connector;
    }

    public Connector getHttpsConnector() throws IOException {
        String keyStorePath = resourceLoader.getResource("classpath:keystore.jks").getFile().getAbsolutePath();
        String trustStorePath = resourceLoader.getResource("classpath:truststore.jks").getFile().getAbsolutePath();

        Connector connector = new Connector(TomcatServletWebServerFactory.DEFAULT_PROTOCOL);
        connector.setPort(8441);
        connector.setScheme("https");
        connector.setSecure(true);
        connector.setAttribute("SSLEnabled", "true");

        SSLHostConfig sslHostConfig = new SSLHostConfig();

        // SERVER CERTIFICATE CONFIGURATION
        sslHostConfig.setCertificateKeystoreFile(keyStorePath);
        sslHostConfig.setCertificateKeystorePassword("");
        sslHostConfig.setCertificateKeyAlias("ssl-server");
        sslHostConfig.setCertificateKeyPassword("");
        sslHostConfig.setTruststoreFile(trustStorePath);
        sslHostConfig.setTruststorePassword("");

        // for http client
        System.setProperty("javax.net.ssl.trustStore", trustStorePath);
        System.setProperty("javax.net.ssl.trustStorePassword", "");

        connector.addSslHostConfig(sslHostConfig);
        return connector;
    }
}
