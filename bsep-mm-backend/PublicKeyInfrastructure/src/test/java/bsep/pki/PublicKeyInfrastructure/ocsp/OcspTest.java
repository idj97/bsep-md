package bsep.pki.PublicKeyInfrastructure.ocsp;

import bsep.pki.PublicKeyInfrastructure.model.enums.RevokeReason;
import bsep.pki.PublicKeyInfrastructure.utility.KeyStoreService;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.ocsp.*;
import org.bouncycastle.crypto.util.SubjectPublicKeyInfoFactory;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CRLReason;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test_h2")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class OcspTest {

    @Autowired
    private KeyStoreService keyStoreService;

    @Test
    public void createOcspRequest() throws OCSPException, OperatorCreationException, CertificateEncodingException, IOException {
        X509Certificate rootCert = keyStoreService.getSingleCertificate("1");
        PublicKey rootPublicKey = rootCert.getPublicKey();
        PrivateKey rootPrivateKey = (PrivateKey) keyStoreService.getKey("1");
        X509Certificate endCert = keyStoreService.getSingleCertificate("3");

        SubjectPublicKeyInfo rootPubInfo = SubjectPublicKeyInfo.getInstance(rootPublicKey.getEncoded());

        X509CertificateHolder endCertHolder = new X509CertificateHolder(endCert.getEncoded());
        BigInteger serialNumber = endCert.getSerialNumber();

        JcaDigestCalculatorProviderBuilder digestCalculatorProviderBuilder = new JcaDigestCalculatorProviderBuilder();
        DigestCalculatorProvider digestCalculatorProvider = digestCalculatorProviderBuilder.build();
        DigestCalculator digestCalculator = digestCalculatorProvider.get(CertificateID.HASH_SHA1);

        CertificateID id = new CertificateID(
                digestCalculator,
                endCertHolder,
                serialNumber);

        OCSPReqBuilder ocspReqBuilder = new OCSPReqBuilder();
        ocspReqBuilder.addRequest(id);
        OCSPReq ocspReq = ocspReqBuilder.build();
        Assert.assertNotNull(ocspReq);

        BasicOCSPRespBuilder basicOCSPRespBuilder = new BasicOCSPRespBuilder(rootPubInfo, digestCalculator);

        Req[] requests = ocspReq.getRequestList();
        for (Req request : requests) {
            CertificateID certId = request.getCertID();

            basicOCSPRespBuilder.addResponse(
                    certId,
                    new RevokedStatus(new Date(), CRLReason.KEY_COMPROMISE.ordinal()));

            basicOCSPRespBuilder.addResponse(
                    certId,
                    CertificateStatus.GOOD);
        }

        JcaContentSignerBuilder jcaContentSignerBuilder = new JcaContentSignerBuilder("SHA256WithRSA");
        jcaContentSignerBuilder.setProvider("BC");
        ContentSigner contentSigner = jcaContentSignerBuilder.build(rootPrivateKey);

        BasicOCSPResp basicOCSPResp =  basicOCSPRespBuilder.build(contentSigner, null, new Date());
        OCSPRespBuilder ocspRespBuilder = new OCSPRespBuilder();
        OCSPResp ocspResp = ocspRespBuilder.build(OCSPRespBuilder.SUCCESSFUL, basicOCSPResp);

        Assert.assertNotNull(ocspResp);
    }



}
