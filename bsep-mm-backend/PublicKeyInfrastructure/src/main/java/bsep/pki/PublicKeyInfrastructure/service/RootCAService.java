package bsep.pki.PublicKeyInfrastructure.service;

import bsep.pki.PublicKeyInfrastructure.data.X509CertificateData;
import bsep.pki.PublicKeyInfrastructure.dto.CADto;
import bsep.pki.PublicKeyInfrastructure.dto.CertificateDto;
import bsep.pki.PublicKeyInfrastructure.model.*;
import bsep.pki.PublicKeyInfrastructure.repository.CARepository;
import bsep.pki.PublicKeyInfrastructure.repository.CertificateRepository;
import bsep.pki.PublicKeyInfrastructure.utility.KeyStoreService;
import bsep.pki.PublicKeyInfrastructure.utility.X500Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.BadRequestException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

@Service
@Transactional(propagation = Propagation.REQUIRED)
public class RootCAService {

    @Value("${root.cn}")
    private String commonName;

    @Value("${root.given_name}")
    private String givenName;

    @Value("${root.surname}")
    private String surname;

    @Value("${root.o}")
    private String organisation;

    @Value("${root.ou}")
    private String orgnisationUnit;

    @Value("${root.c}")
    private String country;

    @Value("${root.email}")
    private String email;

    @Value("${root.validFrom}")
    private String validFromStr;

    @Value("${root.validUntil}")
    private String validUntilStr;

    @Autowired
    private X500Service x500Service;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private CARepository caRepository;

    @Autowired
    private CertificateRepository certificateRepository;

    @Autowired
    private KeyStoreService keyStoreService;

    public void tryCreateRootCA() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        try {
            Date validUntil = sdf.parse(validUntilStr);
            Date validFrom = sdf.parse(validFromStr);
            CertificateDto certificateDto = new CertificateDto(
                    commonName,
                    givenName,
                    surname,
                    organisation,
                    orgnisationUnit,
                    country,
                    email,
                    validFrom,
                    validUntil,
                    null,
                    null,
                    null,
                    null);
            CADto caDto = new CADto(null, null, CAType.ROOT, certificateDto);
            //CADto caDto = new CADto(certificateDto, CAType.ROOT, null, null);
            createRootCA(caDto);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public CA createRootCA(CADto caDto) {
        Optional<CA> optionalCA = caRepository.findByType(CAType.ROOT);
        if (!optionalCA.isPresent()) {
            CertificateDto certificateDto = caDto.getCertificateDto();

            // kreiranje x509 sertifikata
            X509CertificateData x509CertificateData = x500Service
                    .createRootCertificate(certificateDto);

            // kreiranje entiteta Certificate koji odgovara kreiranom x509 sertifikatu
            Certificate certificate = createRootCertificateEntity(
                    certificateDto,
                    x509CertificateData.getSerialNumber());

            // kreiranje entiteta CA i uvezivanje sa entitetom Certificate
            CA ca = new CA();
            ca.setCertificate(certificate);
            certificate.setIssuedForCA(ca);
            ca.setType(CAType.ROOT);

            // usnimi entitet i sertifikat
            ca = caRepository.save(ca);
            x500Service.saveX509Certificate(x509CertificateData);
            return ca;
        } else {
            throw new BadRequestException("Root CA already exists.");
        }
    }

    public Certificate createRootCertificateEntity(CertificateDto certificateDto, String serialNumber) {
        Certificate certificate = new Certificate();
        certificate.setCN(certificateDto.getCommonName());
        certificate.setSurname(certificateDto.getSurname());
        certificate.setUserEmail(certificateDto.getEmail());
        certificate.setGivenName(certificateDto.getGivenName());
        certificate.setC(certificateDto.getCountry());
        certificate.setO(certificateDto.getOrganisation());
        certificate.setOU(certificateDto.getOrganisationUnit());
        certificate.setUserId("system");
        certificate.setValidFrom(certificateDto.getValidFrom());
        certificate.setValidUntil(certificateDto.getValidUntil());
        certificate.setSerialNumber(serialNumber);
        certificate.setKeyStoreAlias(serialNumber);
        certificate.setCertificateType(certificateDto.getCertificateType());

        Extension bcExtension = new Extension();
        bcExtension.setName("Basic Constraint");
        bcExtension.setCertificate(certificate);
        bcExtension.getAttributes().add(
                new ExtensionAttribute(null, "Is Root Certificate Authority.", bcExtension));

        Extension keyUsageExtension = new Extension();
        keyUsageExtension.setName("Key Usage");
        keyUsageExtension.setCertificate(certificate);
        keyUsageExtension.getAttributes().add(
                new ExtensionAttribute(null, "KeyCertSign", keyUsageExtension));
        keyUsageExtension.getAttributes().add(
                new ExtensionAttribute(null, "CrlSign", keyUsageExtension));

        certificate.getExtensions().add(bcExtension);
        certificate.getExtensions().add(keyUsageExtension);
        return certificate;
    }
}
