package bsep.pki.PublicKeyInfrastructure.service;

import bsep.pki.PublicKeyInfrastructure.data.X509CertificateData;
import bsep.pki.PublicKeyInfrastructure.dto.CertificateDto;
import bsep.pki.PublicKeyInfrastructure.dto.RevocationDto;
import bsep.pki.PublicKeyInfrastructure.exception.ApiBadRequestException;
import bsep.pki.PublicKeyInfrastructure.exception.ApiNotFoundException;
import bsep.pki.PublicKeyInfrastructure.model.CA;
import bsep.pki.PublicKeyInfrastructure.model.enums.CAType;
import bsep.pki.PublicKeyInfrastructure.model.Certificate;
import bsep.pki.PublicKeyInfrastructure.model.CertificateRevocation;
import bsep.pki.PublicKeyInfrastructure.repository.CARepository;
import bsep.pki.PublicKeyInfrastructure.repository.CertificateRepository;
import bsep.pki.PublicKeyInfrastructure.utility.KeyStoreService;
import bsep.pki.PublicKeyInfrastructure.utility.X500CrlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.Date;
import java.util.Optional;

@Service
@Transactional(propagation = Propagation.REQUIRED)
public class CRLService {

    @Autowired
    private CARepository caRepository;

    @Autowired
    private CertificateRepository certificateRepository;

    @Autowired
    private KeyStoreService keyStoreService;

    @Autowired
    private X500CrlService x500CrlService;

    @Value("${crl.create}")
    private Boolean createCrlFile;

    public CertificateDto revokeCertificate(RevocationDto revocationDto) {
        Optional<CA> rootCaOptional  = caRepository.findByTypeAndCertificateRevocationNull(CAType.ROOT);
        Optional<Certificate> certificateOptional = certificateRepository
                .findById(revocationDto.getCertificateId());

        if (rootCaOptional.isPresent() && certificateOptional.isPresent()) {
            CA rootCa = rootCaOptional.get();
            Certificate certificate = certificateOptional.get();

            // proveri da li je vec revoke-ovan
            if (certificate.getRevocation() != null)
                throw new ApiBadRequestException();

            // ucitj x509 sertifikat zbog privatnog kljuca
            X509CertificateData rootCertData = keyStoreService
                    .getCaCertificate(rootCa.getCertificate().getKeyStoreAlias());

            // revoke
            x500CrlService.revokeCertificate(
                    new BigInteger(certificate.getSerialNumber()),
                    revocationDto.getRevokeReason(),
                    rootCertData.getPrivateKey());

            // napravi entitet u bazi
            CertificateRevocation certificateRevocation = new CertificateRevocation(
                    null,
                    certificate,
                    new Date(),
                    revocationDto.getRevokeReason());
            certificate.setRevocation(certificateRevocation);
            certificate = certificateRepository.save(certificate);
            return new CertificateDto(certificate);
        } else {
            throw new ApiNotFoundException();
        }
    }

    public void createCRL() {
        if (createCrlFile) {
            // dobavi serial numebr za root CA
            String rootSerialNumber = caRepository
                    .findByType(CAType.ROOT)
                    .get()
                    .getCertificate()
                    .getSerialNumber();

            // ucitaj root sertifikat iz keystore-a
            X509CertificateData x509CertificateData = keyStoreService
                    .getCaCertificate(rootSerialNumber);

            // napravi praznu revocation listu potpisanu root CA privatnim kljucem
            x500CrlService.createRevocationList(
                    x509CertificateData.getX509CertificateChain()[0],
                    x509CertificateData.getPrivateKey());
        }
    }
}
