package bsep.sc.SiemCenter.config.ssl;

import org.apache.catalina.connector.Connector;
import org.apache.tomcat.util.net.SSLHostConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;

@Configuration
public class SSLConfig {

    @Autowired
    ResourceLoader resourceLoader;

    @Bean
    public ServletWebServerFactory servletContainer() throws IOException {
        String keyStorePath = resourceLoader.getResource("classpath:keystore.jks").getFile().getAbsolutePath();;

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

        TomcatConnectorCustomizer customizer = conn -> {
            conn.setRedirectPort(8442);
        };

        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();
        tomcat.addAdditionalTomcatConnectors(connector);
        tomcat.addConnectorCustomizers(customizer);
        return tomcat;
    }
}
