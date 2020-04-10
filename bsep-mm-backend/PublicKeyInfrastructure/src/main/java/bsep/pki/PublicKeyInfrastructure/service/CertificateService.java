package bsep.pki.PublicKeyInfrastructure.service;

import bsep.pki.PublicKeyInfrastructure.data.X509CertificateData;
import bsep.pki.PublicKeyInfrastructure.dto.CertificateDto;
import bsep.pki.PublicKeyInfrastructure.exception.ApiBadRequestException;
import bsep.pki.PublicKeyInfrastructure.exception.ApiNotFoundException;
import bsep.pki.PublicKeyInfrastructure.model.*;
import bsep.pki.PublicKeyInfrastructure.repository.CARepository;
import bsep.pki.PublicKeyInfrastructure.repository.CertificateRepository;
import bsep.pki.PublicKeyInfrastructure.utility.DateService;
import bsep.pki.PublicKeyInfrastructure.utility.X500Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class CertificateService {

    @Autowired
    private X500Service x500Service;

    @Autowired
    private DateService dateService;

    @Autowired
    private CARepository caRepository;

    @Autowired
    private CertificateRepository certificateRepository;


    public CertificateDto createCertificate(CertificateRequest certificateRequest) {
        if (certificateRequest.getCertificateType().equals(CertificateType.SIEM_AGENT)) {
            CA issuerCaOptional = caRepository
                    .findByType(CAType.SIEM_AGENT_ISSUER)
                    .orElseThrow(() -> new ApiNotFoundException("Siem Agent Ca not found."));
            return createCertificate(certificateRequest, issuerCaOptional);
        } else if (certificateRequest.getCertificateType().equals(CertificateType.SIEM_CENTER)) {
            CA issuerCaOptional = caRepository
                    .findByType(CAType.SIEM_CENTER_ISSUER)
                    .orElseThrow(() -> new ApiNotFoundException("Siem Center Ca not found."));
            return createCertificate(certificateRequest, issuerCaOptional);
        } else {
            throw new ApiBadRequestException("Specify Certificate Type!!!");
        }
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

        // uvezivanje subject sertifikata sa issuer sertifikatom
        certificate.setIssuedByCertificate(issuerCertificate);
        issuerCertificate.getIssuerForCertificates().add(certificate);

        return certificate;
    }

}
