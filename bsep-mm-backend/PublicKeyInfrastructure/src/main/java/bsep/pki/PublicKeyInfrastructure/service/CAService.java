package bsep.pki.PublicKeyInfrastructure.service;

import bsep.pki.PublicKeyInfrastructure.data.X509CertificateData;
import bsep.pki.PublicKeyInfrastructure.dto.CADto;
import bsep.pki.PublicKeyInfrastructure.dto.CertificateDto;
import bsep.pki.PublicKeyInfrastructure.dto.CertificateSearchDto;
import bsep.pki.PublicKeyInfrastructure.dto.PageDto;
import bsep.pki.PublicKeyInfrastructure.exception.ApiNotFoundException;
import bsep.pki.PublicKeyInfrastructure.model.*;
import bsep.pki.PublicKeyInfrastructure.repository.CARepository;
import bsep.pki.PublicKeyInfrastructure.repository.CertificateRepository;
import bsep.pki.PublicKeyInfrastructure.utility.KeyStoreService;
import bsep.pki.PublicKeyInfrastructure.utility.PageService;
import bsep.pki.PublicKeyInfrastructure.utility.X500Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    private X500Service x500Service;

    @Autowired
    private KeyStoreService keyStoreService;

    @Autowired
    private CertificateService certificateService;

    @Autowired
    private CARepository caRepository;

    @Autowired
    private CertificateRepository certificateRepository;

    @Autowired
    private PageService pageService;

    @Value("${crl.public.path}")
    private String crlPublicPath;

    public CADto createCA(CADto caDto) {
        Optional<CA> optionalRootCA = caRepository
                .findByTypeAndCertificateRevocationNull(CAType.ROOT);

        if (optionalRootCA.isPresent()) {
            CA rootCa = optionalRootCA.get();
            Certificate issuerCertificate = rootCa.getCertificate();
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
            subjectCa.setParent(rootCa);
            rootCa.getChilds().add(subjectCa);

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
        certificate.setCertificateType(subjectCertificateDto.getCertificateType());

        // uvezivanje subject sertifikata sa issuer sertifikatom
        certificate.setIssuedByCertificate(issuerCertificate);
        issuerCertificate.getIssuerForCertificates().add(certificate);

        // extensions
        Extension bcExtension = new Extension();
        bcExtension.setName("Basic Constraint");
        bcExtension.setCertificate(certificate);
        bcExtension.getAttributes().add(
                new ExtensionAttribute(null, "Is Certificate Authority.", bcExtension));

        Extension keyUsageExtension = new Extension();
        keyUsageExtension.setName("Key Usage");
        keyUsageExtension.setCertificate(certificate);
        keyUsageExtension.getAttributes().add(
                new ExtensionAttribute(null, "KeyCertSign", keyUsageExtension));

        Extension crlDistPointExtension = new Extension();
        crlDistPointExtension.setName("CRL Distribution point");
        crlDistPointExtension.setCertificate(certificate);
        crlDistPointExtension.getAttributes().add(
                new ExtensionAttribute(null, crlPublicPath, crlDistPointExtension));

        certificate.getExtensions().add(bcExtension);
        certificate.getExtensions().add(keyUsageExtension);
        certificate.getExtensions().add(crlDistPointExtension);
        return certificate;
    }

    public PageDto<CADto> getAll(CertificateSearchDto caSearchDto) {
        Pageable pageable = PageRequest.of(caSearchDto.getPage(), caSearchDto.getPageSize());

        List<Certificate> caCertificates = certificateRepository
                .findByCNContainingAndIssuedForCANotNull(caSearchDto.getCommonName());

        // filtriraj sertifikate
        caCertificates = caCertificates
                .stream()
                .filter(c -> {
                    if (caSearchDto.getCaType() != null)
                        return caSearchDto.getCaType().equals(c.getIssuedForCA().getType());
                    return true;
                })
                .filter(c -> {
                    if (caSearchDto.getValidFrom() != null)
                        return c.getValidFrom().after(caSearchDto.getValidFrom());
                    return true;
                })
                .filter(c -> {
                    if (caSearchDto.getValidUntil() != null)
                        return c.getValidUntil().before(caSearchDto.getValidUntil());
                    return true;
                })
                .filter(c -> {
                    if (caSearchDto.getRevoked() == true)
                        return c.getRevocation() != null;
                    return true;
                })
                .collect(Collectors.toList());

        // napravi page
        Page<Certificate> page = pageService.getPage(caCertificates, pageable);

        // pretvori page sertifikata u page ca dto
        List<CADto> caDtos = page
                .getContent()
                .stream()
                .map(c -> new CADto(c.getIssuedForCA()))
                .collect(Collectors.toList());

        return new PageDto<CADto>(caDtos, page.getTotalPages());
    }

    public CADto tryCreateCA(Long id, CAType caType, CertificateType certificateType) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        try {
            Date validUntil = sdf.parse("08-04-2020 21:00");
            Date validFrom = sdf.parse("08-04-2021 21:00");
            CertificateDto certificateDto = new CertificateDto(
            		null,
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
                    null,
                    certificateType,
                    null,
                    null);
            CADto caDto = new CADto(null, id, caType, certificateDto);
            createCA(caDto);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
