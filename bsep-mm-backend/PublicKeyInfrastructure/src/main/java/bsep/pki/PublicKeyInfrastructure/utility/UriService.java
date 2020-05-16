package bsep.pki.PublicKeyInfrastructure.utility;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UriService {
    @Value("#{'${app.ocspResponderUris}'.split(',')}")
    public List<String> ocspResponderUris;

    @Value("${server.port}")
    public String serverPort;

    @Value("${server.address}")
    public String serverAddress;

    @Value("${security.require-ssl}")
    public Boolean isHttps;

    public String getServerAddress() {
        String protocol = isHttps ? "https" : "http";
        return protocol + "://" + serverAddress + ":" + serverPort;
    }

    public String getCertificateAddress(String serialNumber) {
        String serverAddress = getServerAddress();
        return serverAddress + "/api/certificates/download/" + serialNumber;
    }
}
