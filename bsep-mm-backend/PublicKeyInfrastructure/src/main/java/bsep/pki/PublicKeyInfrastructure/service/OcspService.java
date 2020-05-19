package bsep.pki.PublicKeyInfrastructure.service;

import bsep.pki.PublicKeyInfrastructure.dto.CertificateDto;
import bsep.pki.PublicKeyInfrastructure.exception.ApiBadRequestException;
import bsep.pki.PublicKeyInfrastructure.exception.ApiServiceUnavailableException;
import bsep.pki.PublicKeyInfrastructure.model.Certificate;
import bsep.pki.PublicKeyInfrastructure.repository.CertificateRepository;
import bsep.pki.PublicKeyInfrastructure.utility.KeyStoreService;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.ocsp.*;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Optional;

@Service
@Transactional(propagation = Propagation.REQUIRED)
public class OcspService {

    @Autowired
    private CertificateRepository certificateRepository;

    @Autowired
    private KeyStoreService keyStoreService;

    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public byte[] getResponse(byte[] encodedOcspRequest) {
        /*
        pronadji OCSP responder sertifikat iz baze
        ako postoji
            ucitaj OCSP responder X509cert
            ucitaj OCSP private/public key
            napravi DigestCalculator
            napravi BasicOCSPRespBuilder builder(PublicKey, DigestCalculator)

            dekoduj byte[] u OCSPReq
            Req[] iz OCSPReq.requests

            for Req in Req[]
                CertificateID iz Req
                dobavi Certificate iz baze za CertId.serialNumber

                ako postoji
                    revocationReason = null
                    ako je Certificate.revocation.reason != null
                        revocationReason = cert.revocation.reason
                    ako je CertificatePath(certificate ... root).revocation.reason == KeyCompromise
                        revocationReason = cert.revocation.CA_COMPROMISE

                    ako je revocationReason != null
                        builder.addResponse(new RevokedStatus(), reason)
                    else
                        builder.addResponse(GOOD)
                 ako ne postoji
                    builder.addResponse(new UnknownStatus())
           ako ne postoji
                throw new ApiServiceUnavailable()
        */
        OCSPRespBuilder ocspRespBuilder = new OCSPRespBuilder();
        try {
            Optional<Certificate> ocspOptional = certificateRepository.findOcspResponderCertificate();
            if (ocspOptional.isPresent()) {
                Certificate ocspCertEntity = ocspOptional.get();
                X509Certificate ocspCert = keyStoreService.getSingleCertificate(ocspCertEntity.getSerialNumber());
                PrivateKey ocspPrivateKey = (PrivateKey) keyStoreService.getKey(ocspCertEntity.getSerialNumber());
                SubjectPublicKeyInfo ocspPublicKeyInfo = SubjectPublicKeyInfo.getInstance(ocspCert.getPublicKey().getEncoded());

                JcaDigestCalculatorProviderBuilder digestCalculatorProviderBuilder = new JcaDigestCalculatorProviderBuilder();
                DigestCalculatorProvider digestCalculatorProvider = digestCalculatorProviderBuilder.build();
                DigestCalculator digestCalculator = digestCalculatorProvider.get(CertificateID.HASH_SHA1);

                BasicOCSPRespBuilder basicOCSPRespBuilder = new BasicOCSPRespBuilder(ocspPublicKeyInfo, digestCalculator);
                OCSPReq ocspReq = new OCSPReq(encodedOcspRequest);

                for (Req req : ocspReq.getRequestList()) {
                    CertificateID certId = req.getCertID();
                    Optional<Certificate> optCertEntity = certificateRepository.findBySerialNumber(certId.getSerialNumber().toString());

                    if (optCertEntity.isPresent()) {
                        Certificate certEntity = optCertEntity.get();

                        if (certEntity.getRevocation() != null) {
                            basicOCSPRespBuilder.addResponse(certId,
                                    new RevokedStatus(certEntity.getRevocation().getCreatedAt(), certEntity.getRevocation().getRevokeReason().getKey()));
                        } else {
                            basicOCSPRespBuilder.addResponse(certId, CertificateStatus.GOOD);
                        }
                    } else {
                        basicOCSPRespBuilder.addResponse(certId, new UnknownStatus());
                    }
                }

                JcaContentSignerBuilder jcaContentSignerBuilder = new JcaContentSignerBuilder("SHA256WithRSA");
                jcaContentSignerBuilder.setProvider("BC");
                ContentSigner contentSigner = jcaContentSignerBuilder.build(ocspPrivateKey);

                BasicOCSPResp basicOCSPResp =  basicOCSPRespBuilder.build(contentSigner, null, new Date());
                OCSPResp ocspResp = ocspRespBuilder.build(OCSPRespBuilder.SUCCESSFUL, basicOCSPResp);
                return ocspResp.getEncoded();
            } else {
                throw new ApiServiceUnavailableException("OCSP service currently not available");
            }
        } catch (OperatorCreationException e) {
            e.printStackTrace();
        } catch (OCSPException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new ApiBadRequestException();
    }

    public CertificateDto setOcspSigner(String serialNumber) {
        Optional<Certificate> optCert = certificateRepository.findBySerialNumber(serialNumber);
        Optional<Certificate> optOcspCert = certificateRepository.findOcspResponderCertificate();

        if (optCert.isPresent() && optCert.get().getRevocation() == null) {
            Certificate cert = optCert.get();

            if (optOcspCert.isPresent() && optOcspCert.get().getSerialNumber().equals(cert.getSerialNumber())) {
                return new CertificateDto(cert);
            } else {
                if (optOcspCert .isPresent()) {
                    Certificate ocspCert = optOcspCert.get();
                    ocspCert.setOcspResponder(false);
                    certificateRepository.save(ocspCert);
                }
                cert.setOcspResponder(true);
                cert = certificateRepository.save(cert);
                return new CertificateDto(cert);
            }
        } else {
            throw new ApiBadRequestException();
        }
    }
}
