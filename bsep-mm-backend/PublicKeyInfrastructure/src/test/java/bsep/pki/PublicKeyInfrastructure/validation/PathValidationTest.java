package bsep.pki.PublicKeyInfrastructure.validation;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509ContentVerifierProviderBuilder;
import org.bouncycastle.cert.jcajce.JcaX500NameUtil;
import org.bouncycastle.cert.jcajce.JcaX509ContentVerifierProviderBuilder;
import org.bouncycastle.cert.path.CertPath;
import org.bouncycastle.cert.path.CertPathValidation;
import org.bouncycastle.cert.path.CertPathValidationResult;
import org.bouncycastle.cert.path.validations.BasicConstraintsValidation;
import org.bouncycastle.cert.path.validations.KeyUsageValidation;
import org.bouncycastle.cert.path.validations.ParentCertIssuedValidation;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

@RunWith(SpringRunner.class)
public class PathValidationTest {

    @Test
    public void basicConstraintsTest() {
        Security.addProvider(new BouncyCastleProvider());

        try {
            KeyStore keyStore = KeyStore.getInstance(
                    new File("src/main/resources/test.jks"),
                    "a".toCharArray());

            Certificate[] chain =  keyStore.getCertificateChain("end-invalid2 (end-valid)");
            X509CertificateHolder[] x509CertificateHolders = new X509CertificateHolder[chain.length];
            for (int i=0; i<chain.length; i++) {
                x509CertificateHolders[i] = new X509CertificateHolder(chain[i].getEncoded());
                System.out.println(chain[i].toString());
            }
            //x509CertificateHolders[1] = x509CertificateHolders[0];

            CertPath certPath = new CertPath(x509CertificateHolders);
            X509ContentVerifierProviderBuilder verifier = new JcaX509ContentVerifierProviderBuilder().setProvider(BouncyCastleProvider.PROVIDER_NAME);

            CertPathValidationResult certPathValidationResult = certPath.validate(
                    new CertPathValidation[]{
                            new ParentCertIssuedValidation(verifier),
                            new BasicConstraintsValidation(true),
                            new KeyUsageValidation(true)
                    });


            Assert.assertFalse(certPathValidationResult.isValid());
            System.out.println(certPathValidationResult.getFailingCertIndex());
            System.out.println(certPathValidationResult.getCause());

        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        }
    }
}
