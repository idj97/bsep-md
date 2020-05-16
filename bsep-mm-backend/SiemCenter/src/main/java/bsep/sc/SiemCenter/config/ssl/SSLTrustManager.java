package bsep.sc.SiemCenter.config.ssl;

import bsep.sc.SiemCenter.util.KeyStoreUtil;

import javax.net.ssl.X509TrustManager;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SSLTrustManager implements X509TrustManager {
    private String classpath;
    private String trustStorePath;
    private String trustStorePassword;
    private String ocspCertAlias;
    private String rootCertAlias;

    public SSLTrustManager() {
        super();
        classpath = System.getProperty("java.class.path").split(":")[0];
        trustStorePath = classpath + "/truststore.jks";
        trustStorePassword = "";
        ocspCertAlias = "ocsp";
        rootCertAlias = "root";
    }

    @Override
    public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
        validation(x509Certificates);
    }

    @Override
    public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
        validation(x509Certificates);
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        System.out.println("ACCEPTED ISSUERS");
        return new X509Certificate[0];
    }

    private void validation(X509Certificate[] x509Certificates) throws CertificateException {
        X509Certificate rootCert = KeyStoreUtil.getSingleCertificate(trustStorePath, rootCertAlias, trustStorePassword);
        X509Certificate ocspCert = KeyStoreUtil.getSingleCertificate(trustStorePath, ocspCertAlias, trustStorePassword);

        Set<TrustAnchor> trustAnchors = new HashSet<>();
        trustAnchors.add(new TrustAnchor(rootCert, null));

        List<X509Certificate> certChain = new ArrayList<>();
        for (int i = 0; i < x509Certificates.length; i++)
            certChain.add(x509Certificates[i]);

        CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
        CertPath certPath = certFactory.generateCertPath(certChain);

        try {
            CertPathValidator certPathValidator = CertPathValidator.getInstance("PKIX");
            PKIXParameters pkixParams = new PKIXParameters(trustAnchors);
            PKIXRevocationChecker pkixRevocationChecker = (PKIXRevocationChecker) certPathValidator.getRevocationChecker();
            pkixRevocationChecker.setOcspResponder(new URI("http://localhost:8081/ocsp"));
            pkixRevocationChecker.setOcspResponderCert(ocspCert);
            pkixParams.addCertPathChecker(pkixRevocationChecker);
            certPathValidator.validate(certPath, pkixParams);
        } catch (NoSuchAlgorithmException e) {
            throw new CertificateException();
        } catch (InvalidAlgorithmParameterException e) {
            throw new CertificateException();
        } catch (URISyntaxException e) {
            throw new CertificateException();
        } catch (CertPathValidatorException e) {
            System.out.println(e.getReason().toString());
            System.out.println("VALIDATION FAILED");
            throw new CertificateException("VALIDATION FAILED.");
        }
    }
}
