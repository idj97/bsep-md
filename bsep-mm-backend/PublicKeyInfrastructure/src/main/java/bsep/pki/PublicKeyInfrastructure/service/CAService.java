package bsep.pki.PublicKeyInfrastructure.service;

import bsep.pki.PublicKeyInfrastructure.data.X509CertificateData;
import bsep.pki.PublicKeyInfrastructure.dto.CADto;
import bsep.pki.PublicKeyInfrastructure.dto.CertificateDto;
import bsep.pki.PublicKeyInfrastructure.exception.ApiNotFoundException;
import bsep.pki.PublicKeyInfrastructure.model.CA;
import bsep.pki.PublicKeyInfrastructure.model.CAType;
import bsep.pki.PublicKeyInfrastructure.model.Certificate;
import bsep.pki.PublicKeyInfrastructure.model.CertificateType;
import bsep.pki.PublicKeyInfrastructure.repository.CARepository;
import bsep.pki.PublicKeyInfrastructure.utility.CertificateGenerationService;
import bsep.pki.PublicKeyInfrastructure.utility.KeyStoreService;
import bsep.pki.PublicKeyInfrastructure.utility.X500Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(propagation = Propagation.REQUIRED)
public class CAService {

    @Autowired
    private CertificateGenerationService certificateGenerationService;

    @Autowired
    private X500Service x500Service;

    @Autowired
    private KeyStoreService keyStoreService;

    @Autowired
    private CertificateService certificateService;

    @Autowired
    private CARepository caRepository;

    public CADto createCA(CADto caDto) {
        Optional<CA> optionalCA = caRepository.findById(caDto.getCaIssuerId());
        if (optionalCA.isPresent()) {
            CA issuerCa = optionalCA.get();
            Certificate issuerCertificate = issuerCa.getCertificate();
            CertificateDto subjectCertificateDto = caDto.getCertificateDto();

            // kreiraj subject x509 sertifikat potpisanog od strane issuer (ca) x509 sertifikata
            X509CertificateData subjectX509CertificateData = x500Service
                    .createCertificate(subjectCertificateDto, issuerCertificate);

            // kreiranje entiteta Certificate koji odgovara kreiranom x509 sertifikatu
            Certificate subjectCertificate = createCertificateEntity(
                    subjectCertificateDto,
                    issuerCertificate,
                    subjectX509CertificateData.getSerialNumber());

            // kreirnje CA entiteta
            CA subjectCa = new CA();
            subjectCa.setType(caDto.getCaType());

            // uvezi sa subject sertifikatom
            subjectCa.setCertificate(subjectCertificate);
            subjectCertificate.setIssuedForCA(subjectCa);

            // uvezi sa issuer ca
            subjectCa.setParent(issuerCa);
            issuerCa.getChilds().add(subjectCa);

            subjectCa = caRepository.save(subjectCa);
            x500Service.saveX509Certificate(subjectX509CertificateData);
            return new CADto(subjectCa);
        } else {
            throw new ApiNotFoundException("CA issuer not found.");
        }
    }

    public Certificate createCertificateEntity(
            CertificateDto subjectCertificateDto,
            Certificate issuerCertificate,
            String serialNumber)
    {
        Certificate certificate = new Certificate();

        // osnovni podaci
        certificate.setCN(subjectCertificateDto.getCommonName());
        certificate.setSurname(subjectCertificateDto.getSurname());
        certificate.setUserEmail(subjectCertificateDto.getEmail());
        certificate.setGivenName(subjectCertificateDto.getGivenName());
        certificate.setC(subjectCertificateDto.getCountry());
        certificate.setO(subjectCertificateDto.getOrganisation());
        certificate.setOU(subjectCertificateDto.getOrganisationUnit());
        certificate.setUserId("test"); // TODO postaviti user id iz keycloak context-a
        certificate.setValidFrom(subjectCertificateDto.getValidFrom());
        certificate.setValidUntil(subjectCertificateDto.getValidUntil());
        certificate.setSerialNumber(serialNumber);
        certificate.setKeyStoreAlias(serialNumber);

        // uvezivanje subject sertifikata sa issuer sertifikatom
        certificate.setIssuedByCertificate(issuerCertificate);
        issuerCertificate.getIssuerForCertificates().add(certificate);

        return certificate;
    }

    public List<CADto> getAll() {
        return caRepository.findAll()
                .stream()
                .map(CADto::new)
                .collect(Collectors.toList());
    }

    public CADto tryCreateCA(Long id) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        try {
            Date validUntil = sdf.parse("08-04-2020 21:00");
            Date validFrom = sdf.parse("08-04-2021 21:00");
            CertificateDto certificateDto = new CertificateDto(
                    "*.google-ca.com",
                    "google",
                    "ca",
                    "abc",
                    "google PKI",
                    "usa",
                    "google-pki@gmail.com",
                    validFrom,
                    validUntil,
                    null,
                    CertificateType.UNDEFINED,
                    null);
            // CADto caDto = new CADto(nucertificateDto, CAType.UNDEFINED, id, null);
            CADto caDto = new CADto(null, id, CAType.UNDEFINED, certificateDto);
            createCA(caDto);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
