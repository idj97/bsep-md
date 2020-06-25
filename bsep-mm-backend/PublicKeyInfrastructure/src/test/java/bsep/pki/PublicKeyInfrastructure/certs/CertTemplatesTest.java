package bsep.pki.PublicKeyInfrastructure.certs;

import bsep.pki.PublicKeyInfrastructure.model.Certificate;
import bsep.pki.PublicKeyInfrastructure.repository.CertificateRepository;
import bsep.pki.PublicKeyInfrastructure.service.TemplateService;
import bsep.pki.PublicKeyInfrastructure.utility.KeyStoreService;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.io.StringWriter;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Optional;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test_h2")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class CertTemplatesTest {

    @Autowired
    private TemplateService templateService;

    @Autowired
    private CertificateRepository certificateRepository;

    @Autowired
    private KeyStoreService keyStoreService;

    @Test
    public void test() {
        Assert.assertEquals(4, templateService.getAll().size());
    }

    @Test
    public void pemTest() throws IOException, CertificateEncodingException {
        Optional<Certificate> certificate = certificateRepository.findBySerialNumber("1");
        Assert.assertTrue(certificate.isPresent());

        Certificate root = certificate.get();
        System.out.println(root.getCommonName());

        X509Certificate x509Certificate = keyStoreService.getSingleCertificate("1");

        StringWriter sw = new StringWriter();
        try (JcaPEMWriter pw = new JcaPEMWriter(sw)) {
            pw.writeObject(x509Certificate);
        }
        System.out.println( sw.toString());

        System.out.println("----------------------------");

        X509Certificate[] x509chain = keyStoreService.getCertificateChain("5");
        sw = new StringWriter();
        try (JcaPEMWriter pw = new JcaPEMWriter(sw)) {
            for (X509Certificate x509Certificate1 : x509chain) {
                pw.writeObject(x509Certificate1);
            }
        }
        System.out.println( sw.toString());
    }
}
