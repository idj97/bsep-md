package bsep.pki.PublicKeyInfrastructure.dto;

import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.cert.jcajce.JcaX500NameUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

@RunWith(SpringRunner.class)
public class NameDtoTest {

    @Test
    public void test() {
        String commonName = "test name";
        NameDto nameDto = new NameDto();
        nameDto.setCommonName(commonName);

        X500Name x500Name = nameDto.getBCX500Name();

        RDN[] rdns = x500Name.getRDNs(BCStyle.CN);
        for (RDN rdn : rdns) {
            System.out.println(rdn.getFirst().getValue().toString());
        }

        Assert.assertEquals(commonName, x500Name.getRDNs(BCStyle.CN));
    }

    @Test
    public void testX500NameRetrival() {
        try {
            KeyStore keyStore = KeyStore.getInstance(new File("src/main/resources/test.jks"), "a".toCharArray());

            X509Certificate x509Certificate =  (X509Certificate) keyStore.getCertificate("a");
            X500Name x500Name = JcaX500NameUtil.getSubject(x509Certificate);

            Assert.assertNotNull(x500Name);

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
