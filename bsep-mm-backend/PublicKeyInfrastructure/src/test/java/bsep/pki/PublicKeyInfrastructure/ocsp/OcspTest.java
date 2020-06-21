package bsep.pki.PublicKeyInfrastructure.ocsp;

import bsep.pki.PublicKeyInfrastructure.dto.CreateCertificateDto;
import bsep.pki.PublicKeyInfrastructure.dto.NameDto;
import bsep.pki.PublicKeyInfrastructure.dto.RevocationDto;
import bsep.pki.PublicKeyInfrastructure.dto.extensions.*;
import bsep.pki.PublicKeyInfrastructure.exception.ApiBadRequestException;
import bsep.pki.PublicKeyInfrastructure.exception.ApiServiceUnavailableException;
import bsep.pki.PublicKeyInfrastructure.model.enums.RevokeReason;
import bsep.pki.PublicKeyInfrastructure.service.CertificateService;
import bsep.pki.PublicKeyInfrastructure.service.OcspService;
import bsep.pki.PublicKeyInfrastructure.utility.KeyStoreService;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.ocsp.*;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.*;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaContentVerifierProviderBuilder;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigInteger;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.cert.CRLReason;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test_h2")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class OcspTest {

    @Autowired
    private KeyStoreService keyStoreService;

    @Autowired
    private CertificateService certificateService;

    @Autowired
    private OcspService ocspService;

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

    public void createRootCert() {
        Security.addProvider(new BouncyCastleProvider());
        NameDto nameDto = new NameDto();
        nameDto.setCommonName("root");

        KeyUsageDto keyUsageDto = new KeyUsageDto();
        keyUsageDto.setKeyCertSign(true);

        BasicConstraintsDto basicConstraintsDto = new BasicConstraintsDto(true, null);
        basicConstraintsDto.setIsCritical(true);

        List<AbstractExtensionDto> extensionDtos = new ArrayList<>() {
            {
                add(basicConstraintsDto);
                add(keyUsageDto);
                add(new SubjectKeyIdentifierDto());
            }
        };

        CreateCertificateDto createCertificateDto = new CreateCertificateDto(
                "RSA",
                2048,
                "SHA256withRSA",
                "04-05-2020 00:00",
                "05-05-2021 00:00",
                true,
                "1",
                nameDto,
                extensionDtos,
                null,
                null
        );
        certificateService.create(createCertificateDto);
    }

    public void createOcspCert() {
        Security.addProvider(new BouncyCastleProvider());
        NameDto nameDto = new NameDto();
        nameDto.setCommonName("ocsp");

        KeyUsageDto keyUsageDto = new KeyUsageDto();
        keyUsageDto.setDigitalSignature(true);

        ExtendedKeyUsageDto extendedKeyUsageDto = new ExtendedKeyUsageDto();
        extendedKeyUsageDto.setOCSPSigning(true);

        List<AbstractExtensionDto> extensionDtos = new ArrayList<>() {
            {
                add(keyUsageDto);
                add(extendedKeyUsageDto);
                add(new AuthorityInfoAccessDto());
                add(new AuthorityKeyIdentifierDto());
                add(new SubjectKeyIdentifierDto());
            }
        };

        CreateCertificateDto createCertificateDto = new CreateCertificateDto(
                "RSA",
                2048,
                "SHA256withRSA",
                "04-05-2020 00:00",
                "05-05-2021 00:00",
                false,
                "2",
                nameDto,
                extensionDtos,
                "1",
                null
        );
        certificateService.create(createCertificateDto);
    }

    public void createEndCert() {
        Security.addProvider(new BouncyCastleProvider());
        NameDto nameDto = new NameDto();
        nameDto.setCommonName("end");

        KeyUsageDto keyUsageDto = new KeyUsageDto();
        keyUsageDto.setDigitalSignature(true);
        keyUsageDto.setKeyEncipherment(true);

        ExtendedKeyUsageDto extendedKeyUsageDto = new ExtendedKeyUsageDto();
        extendedKeyUsageDto.setServerAuth(true);

        List<AbstractExtensionDto> extensionDtos = new ArrayList<>() {
            {
                add(keyUsageDto);
                add(extendedKeyUsageDto);
                add(new AuthorityInfoAccessDto());
                add(new AuthorityKeyIdentifierDto());
                add(new SubjectKeyIdentifierDto());
            }
        };

        CreateCertificateDto createCertificateDto = new CreateCertificateDto(
                "RSA",
                2048,
                "SHA256withRSA",
                "04-05-2020 00:00",
                "05-05-2021 00:00",
                false,
                "3",
                nameDto,
                extensionDtos,
                "1",
                null
        );
        certificateService.create(createCertificateDto);
    }

    public OCSPReq createOcspReq(X509CertificateHolder certHolder) {
        try {
            JcaDigestCalculatorProviderBuilder digestCalculatorProviderBuilder = new JcaDigestCalculatorProviderBuilder();
            DigestCalculatorProvider digestCalculatorProvider = digestCalculatorProviderBuilder.build();
            DigestCalculator digestCalculator = digestCalculatorProvider.get(CertificateID.HASH_SHA1);

            CertificateID certId = new CertificateID(
                    digestCalculator,
                    certHolder,
                    certHolder.getSerialNumber());

            OCSPReqBuilder ocspReqBuilder = new OCSPReqBuilder();
            ocspReqBuilder.addRequest(certId);
            OCSPReq ocspReq = ocspReqBuilder.build();
            return ocspReq;
        } catch (OperatorCreationException e) {
            e.printStackTrace();
        } catch (OCSPException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Test(expected = ApiBadRequestException.class)
    @Transactional(propagation = Propagation.REQUIRED)
    public void givenInvalidOcspRequest_whenCheckOcsp_expectBadRequestException() throws
            CertificateEncodingException,
            IOException
    {
        createRootCert();
        createEndCert();
        ocspService.setOcspSigner("2");

        X509Certificate endCert = keyStoreService.getSingleCertificate("3");
        X509CertificateHolder endCertHolder = new X509CertificateHolder(endCert.getEncoded());

        OCSPReq ocspReq = createOcspReq(endCertHolder);
        if (ocspReq == null)
            Assert.fail("Ocsp request is null");

        ocspService.getResponse(new String().getBytes());
    }


    @Test(expected = ApiServiceUnavailableException.class)
    @Transactional(propagation = Propagation.REQUIRED)
    public void givenOcspNotConfigured_whenCheckOcsp_expectServiceNotAvailableException() throws
            CertificateEncodingException,
            IOException
    {
        createRootCert();
        createEndCert();

        X509Certificate endCert = keyStoreService.getSingleCertificate("3");
        X509CertificateHolder endCertHolder = new X509CertificateHolder(endCert.getEncoded());

        OCSPReq ocspReq = createOcspReq(endCertHolder);
        if (ocspReq == null)
            Assert.fail("Ocsp request is null");

        ocspService.getResponse(ocspReq.getEncoded());
    }

    @Test
    @Transactional(propagation = Propagation.REQUIRED)
    public void givenRevokedSN_whenCheckOcsp_expectOcspWithRevokedReason() throws
            CertificateEncodingException,
            IOException,
            OCSPException,
            OperatorCreationException
    {
        createRootCert();
        createOcspCert();
        createEndCert();

        ocspService.setOcspSigner("2");
        certificateService.revoke(new RevocationDto(
                null,
                null,
                "3",
                RevokeReason.KEY_COMPROMISE,
                new Date()
        ));

        X509Certificate ocspCert = keyStoreService.getSingleCertificate("2");
        X509Certificate endCert = keyStoreService.getSingleCertificate("3");
        X509CertificateHolder endCertHolder = new X509CertificateHolder(endCert.getEncoded());

        OCSPReq ocspReq = createOcspReq(endCertHolder);
        if (ocspReq == null)
            Assert.fail("Ocsp request is null");

        byte[] ocspResponse = ocspService.getResponse(ocspReq.getEncoded());
        OCSPResp ocspResp = new OCSPResp(ocspResponse);
        BasicOCSPResp basicOCSPResp = (BasicOCSPResp) ocspResp.getResponseObject();

        JcaContentVerifierProviderBuilder jcaContentVerifierProviderBuilder = new JcaContentVerifierProviderBuilder();
        jcaContentVerifierProviderBuilder.setProvider("BC");
        ContentVerifierProvider contentVerifierProvider = jcaContentVerifierProviderBuilder.build(ocspCert.getPublicKey());
        boolean isValid = basicOCSPResp.isSignatureValid(contentVerifierProvider);
        Assert.assertTrue(isValid);

        SingleResp singleResp = basicOCSPResp.getResponses()[0];
        Assert.assertEquals("3", singleResp.getCertID().getSerialNumber().toString());
        Assert.assertEquals(
                (int) RevokeReason.KEY_COMPROMISE.getKey(),
                ((RevokedStatus) singleResp.getCertStatus()).getRevocationReason());
    }

    @Test
    @Transactional(propagation = Propagation.REQUIRED)
    public void givenValidSN_whenCheckOcsp_expectOcspWithStatusGood() throws
            CertificateEncodingException,
            IOException,
            OCSPException,
            OperatorCreationException
    {
        createRootCert();
        createOcspCert();
        createEndCert();

        ocspService.setOcspSigner("2");
//        crlService.revoke(new RevocationDto(
//                null,
//                null,
//                "3",
//                RevokeReason.KEY_COMPROMISE,
//                new Date()
//        ));
        X509Certificate ocspCert = keyStoreService.getSingleCertificate("2");
        X509Certificate endCert = keyStoreService.getSingleCertificate("3");
        X509CertificateHolder endCertHolder = new X509CertificateHolder(endCert.getEncoded());

        OCSPReq ocspReq = createOcspReq(endCertHolder);
        if (ocspReq == null)
            Assert.fail("Ocsp request is null");

        byte[] ocspResponse = ocspService.getResponse(ocspReq.getEncoded());
        OCSPResp ocspResp = new OCSPResp(ocspResponse);
        BasicOCSPResp basicOCSPResp = (BasicOCSPResp) ocspResp.getResponseObject();

        JcaContentVerifierProviderBuilder jcaContentVerifierProviderBuilder = new JcaContentVerifierProviderBuilder();
        jcaContentVerifierProviderBuilder.setProvider("BC");
        ContentVerifierProvider contentVerifierProvider = jcaContentVerifierProviderBuilder.build(ocspCert.getPublicKey());
        boolean isValid = basicOCSPResp.isSignatureValid(contentVerifierProvider);
        Assert.assertTrue(isValid);

        SingleResp singleResp = basicOCSPResp.getResponses()[0];
        Assert.assertEquals("3", singleResp.getCertID().getSerialNumber().toString());
        Assert.assertEquals(CertificateStatus.GOOD, singleResp.getCertStatus());
    }
}
