package bsep.sa.SiemAgent.config;

import bsep.sa.SiemAgent.util.KeyStoreUtil;
import org.springframework.core.io.ResourceLoader;

import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.*;
import java.util.*;

public class SSLTrustManager implements X509TrustManager {
    private String classpath;
    private String trustStorePath;
    private String trustStorePassword;
    private String ocspCertAlias;
    private String rootCertAlias;
    private Object lock;


    public SSLTrustManager() {
        super();
        String os = System.getProperty("os.name");
        if (os.toLowerCase().contains("linux")) {
            classpath = System.getProperty("java.class.path").split(":")[0];

        }
        else if (os.toLowerCase().contains("win")) {
            classpath = System.getProperty("java.class.path").split(";")[0];
        }

        trustStorePath = classpath + "/truststore.jks";
        trustStorePassword = "";
        ocspCertAlias = "ocsp";
        rootCertAlias = "root";
    }

    @Override
    public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
        try {
            validation(x509Certificates);
        } catch (CertificateException ex) {
            System.out.println("Client validation failed, reason: " + ex.getMessage() + " at " + new Date());
            throw ex;
        }
    }

    @Override
    public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
        try {
            validation(x509Certificates);
        } catch (CertificateException ex) {
            System.out.println("Server validation failed, reason: " + ex.getMessage() + " at " + new Date());
            throw ex;
        }
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[0];
    }

    private void validation(X509Certificate[] x509Certificates) throws CertificateException {
        X509Certificate rootCert = KeyStoreUtil.getSingleCertificate(trustStorePath, rootCertAlias, trustStorePassword);
        X509Certificate ocspCert = KeyStoreUtil.getSingleCertificate(trustStorePath, ocspCertAlias, trustStorePassword);

        Set<TrustAnchor> trustAnchors = new HashSet<>();
        trustAnchors.add(new TrustAnchor(rootCert, null));

        List<X509Certificate> certChain = new ArrayList<>();
        for (int i = 0; i < x509Certificates.length-1; i++)
            certChain.add(x509Certificates[i]);

        CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
        CertPath certPath = certFactory.generateCertPath(certChain);

        try {
            CertPathValidator certPathValidator = CertPathValidator.getInstance("PKIX");

            PKIXRevocationChecker pkixRevocationChecker = (PKIXRevocationChecker) certPathValidator.getRevocationChecker();
            HashSet<PKIXRevocationChecker.Option> options = new HashSet<>();
            options.add(PKIXRevocationChecker.Option.NO_FALLBACK);
            pkixRevocationChecker.setOptions(options);
            pkixRevocationChecker.setOcspResponderCert(ocspCert);

            PKIXParameters pkixParams = new PKIXParameters(trustAnchors);
            pkixParams.addCertPathChecker(pkixRevocationChecker);
            pkixParams.setDate(new Date());
            certPathValidator.validate(certPath, pkixParams);
        } catch (NoSuchAlgorithmException e) {
            throw new CertificateException("No such algorithm.");
        } catch (InvalidAlgorithmParameterException e) {
            throw new CertificateException("Invalid algorithm parameter.");
        } catch (CertPathValidatorException e) {
            throw new CertificateException(e.getReason().toString());
        }
    }
}