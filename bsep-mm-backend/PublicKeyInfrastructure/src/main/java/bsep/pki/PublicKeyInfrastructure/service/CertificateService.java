package bsep.pki.PublicKeyInfrastructure.service;

import bsep.pki.PublicKeyInfrastructure.data.X509CertificateData;
import bsep.pki.PublicKeyInfrastructure.dto.CertificateDto;
import bsep.pki.PublicKeyInfrastructure.dto.CertificateSearchDto;
import bsep.pki.PublicKeyInfrastructure.dto.PageDto;
import bsep.pki.PublicKeyInfrastructure.exception.ApiNotFoundException;
import bsep.pki.PublicKeyInfrastructure.model.*;
import bsep.pki.PublicKeyInfrastructure.model.enums.CertificateType;
import bsep.pki.PublicKeyInfrastructure.repository.CARepository;
import bsep.pki.PublicKeyInfrastructure.repository.CertificateRepository;
import bsep.pki.PublicKeyInfrastructure.utility.DateService;
import bsep.pki.PublicKeyInfrastructure.utility.KeyStoreService;
import bsep.pki.PublicKeyInfrastructure.utility.X500Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.PrivateKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(propagation = Propagation.REQUIRED)
public class CertificateService {

    @Autowired
    private X500Service x500Service;

    @Autowired
    private DateService dateService;

    @Autowired
    private CARepository caRepository;

    @Autowired
    private CertificateRepository certificateRepository;

    @Autowired
    private KeyStoreService keyStoreService;

    @Value("${crl.public.path}")
    private String crlPublicPath;
    
    @Value("${certs.endpoint}")
    private String certEndpoint;

    public CertificateDto createCertificate(CertificateRequest certificateRequest, Long issuerId) {

        CA issuer = caRepository
                .findById(issuerId)
                .orElseThrow(() -> new ApiNotFoundException("CA issuer not found"));

        return createCertificate(certificateRequest, issuer);
    }

    public CertificateDto createCertificate(CertificateRequest certificateRequest, CA issuerCa) {
        CertificateDto certificateDto = new CertificateDto();

        certificateDto.setCertificateType(certificateRequest.getCertificateType());
        certificateDto.setCommonName(certificateRequest.getCommonName());
        certificateDto.setCountry(certificateRequest.getCountry());
        certificateDto.setOrganisation(certificateRequest.getOrganisation());
        certificateDto.setGivenName(certificateRequest.getGivenName());
        certificateDto.setSurname(certificateRequest.getSurname());
        certificateDto.setOrganisationUnit(certificateRequest.getOrganisationUnit());
        certificateDto.setEmail(certificateRequest.getEmail());

        Date now = new Date();
        certificateDto.setValidFrom(now);

        Date until = dateService.addMonths(now, 6);
        certificateDto.setValidUntil(until);

        X509CertificateData subjectX509Data = x500Service.createCertificate(
                certificateDto, issuerCa.getCertificate());

        Certificate certificate = createCertificateEntity(
                certificateDto,
                issuerCa.getCertificate(),
                subjectX509Data.getSerialNumber());

        certificateRequest.setCertificate(certificate);
        certificate.setCertificateRequest(certificateRequest);

        certificate = certificateRepository.save(certificate);
        x500Service.saveX509Certificate(subjectX509Data);
        return new CertificateDto(certificate);
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

        CertificateExtension bcExtension = new CertificateExtension();
        bcExtension.setName("Basic Constraint");
        //bcExtension.setCertificate(certificate);
        bcExtension.getAttributes().add(
                new ExtensionAttribute(null, "Not Certificate Authority."));

        CertificateExtension keyUsageExtension = new CertificateExtension();
        keyUsageExtension.setName("Key Usage");
        //keyUsageExtension.setCertificate(certificate);

        if (certificate.getCertificateType().equals(CertificateType.SIEM_AGENT)) {
            keyUsageExtension.getAttributes().add(
                    new ExtensionAttribute(null, "DataSign"));
        }

        keyUsageExtension.getAttributes().add(
                new ExtensionAttribute(null, "KeyEncipherment"));

        CertificateExtension crlDistPointExtension = new CertificateExtension();
        crlDistPointExtension.setName("CRL Distribution point");
        //crlDistPointExtension.setCertificate(certificate);
        crlDistPointExtension.getAttributes().add(
                new ExtensionAttribute(null, crlPublicPath));
        
        CertificateExtension aiaExtension = new CertificateExtension();
        aiaExtension.setName("Authority Information Access");
        //aiaExtension.setCertificate(certificate);
        aiaExtension.getAttributes().add(
                new ExtensionAttribute(
                        null,
                        "URL: " + certEndpoint + issuerCertificate.getSerialNumber()));

        certificate.getExtensions().add(bcExtension);
        certificate.getExtensions().add(keyUsageExtension);
        certificate.getExtensions().add(crlDistPointExtension);
        certificate.getExtensions().add(aiaExtension);

        return certificate;
    }

    public PageDto<CertificateDto> search(CertificateSearchDto certificateSearchDto) {
        // pripremi page request podatke
        Pageable pageable = PageRequest.of(
                certificateSearchDto.getPage(),
                certificateSearchDto.getPageSize());

        // pretraga
        Page<Certificate> certificates = certificateRepository.search(
                certificateSearchDto.getCommonName(),
                certificateSearchDto.getRevoked(),
                certificateSearchDto.getIsCa(),
                certificateSearchDto.getValidFrom(),
                certificateSearchDto.getValidUntil(),
                certificateSearchDto.getCertificateType(),
                pageable);

        // pretvori u certificate dto
        PageDto<CertificateDto> pageDto = new PageDto<>();
        pageDto.setItems(certificates
                .getContent()
                .stream()
                .map(CertificateDto::new)
                .collect(Collectors.toList()));
        pageDto.setNumberOfPages(certificates.getTotalPages());

        return pageDto;
    }

    public InputStreamResource getCertFileBySerialNumber(String serialNumber) {
        Optional<Certificate> optionalCertificate = certificateRepository.findBySerialNumber(serialNumber);
        if (optionalCertificate.isPresent()) {
            Certificate certificate = optionalCertificate.get();
            X509Certificate x509Certificate = keyStoreService.getCertificate(
                    certificate.getSerialNumber()).getX509CertificateChain()[0];

            byte[] binary = null;
            try {
                binary = x509Certificate.getEncoded();
            } catch (CertificateEncodingException e) {
                e.printStackTrace();
            }

            return new InputStreamResource(new ByteArrayInputStream(binary));
        } else {
            throw new ApiNotFoundException("Cert not found.");
        }
    }

    public InputStreamResource getCertPKCS12BySerialNumber(String serialNumber) {
        Optional<Certificate> optionalCertificate = certificateRepository.findBySerialNumber(serialNumber);
        if (optionalCertificate.isPresent()) {
            Certificate certificate = optionalCertificate.get();

            X509Certificate[] chain = keyStoreService
                    .getCertificate(certificate.getSerialNumber())
                    .getX509CertificateChain();

            PrivateKey privateKey = (PrivateKey) keyStoreService
                    .getKey(certificate.getSerialNumber());

            InputStream pkcs12InStream = keyStoreService.getPkcs12InputStream(
                    chain, privateKey, certificate.getSerialNumber());

            return new InputStreamResource(pkcs12InStream);
        } else {
            throw new ApiNotFoundException("Cert not found");
        }
    }
}