package bsep.pki.PublicKeyInfrastructure.service;

import bsep.pki.PublicKeyInfrastructure.data.X509CertificateData;
import bsep.pki.PublicKeyInfrastructure.dto.CADto;
import bsep.pki.PublicKeyInfrastructure.dto.CertificateDto;
import bsep.pki.PublicKeyInfrastructure.exception.ApiBadRequestException;
import bsep.pki.PublicKeyInfrastructure.model.*;
import bsep.pki.PublicKeyInfrastructure.model.enums.CAType;
import bsep.pki.PublicKeyInfrastructure.model.enums.CertificateType;
import bsep.pki.PublicKeyInfrastructure.repository.CARepository;
import bsep.pki.PublicKeyInfrastructure.repository.CertificateRepository;
import bsep.pki.PublicKeyInfrastructure.utility.DateService;
import bsep.pki.PublicKeyInfrastructure.utility.KeyStoreService;
import bsep.pki.PublicKeyInfrastructure.utility.X500Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

//TODO: DELETE
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
    private CARepository caRepository;

    @Autowired
    private CertificateRepository certificateRepository;

    @Autowired
    private KeyStoreService keyStoreService;

    @Autowired
    private DateService dateService;

    public void tryCreateRootCA() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        try {
            Date validUntil = sdf.parse(validUntilStr);
            Date validFrom = sdf.parse(validFromStr);
            CertificateDto certificateDto = new CertificateDto(
            		null,
                    commonName,
                    givenName,
                    surname,
                    organisation,
                    orgnisationUnit,
                    country,
                    email,
                    validFrom,
                    0,
                    validUntil,
                    null,
                    null,
                    CertificateType.ROOT,
                    null,
                    null);
            CADto caDto = new CADto(null, null, CAType.ROOT, certificateDto);
            //CADto caDto = new CADto(certificateDto, CAType.ROOT, null, null);
            createRootCA(caDto);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public CADto createRootCA(CADto caDto) {
        List<CA> optionalCA = caRepository.findByTypeAndCertificateRevocationNull(CAType.ROOT);
        if (optionalCA.size() == 0) {
            CertificateDto certificateDto = caDto.getCertificateDto();

            // if the end date wasn't directly specified, but was given in number of months
            if(caDto.getCertificateDto().getValidUntil() == null) {
                caDto.getCertificateDto().setValidUntil(
                        dateService.addMonths(
                                caDto.getCertificateDto().getValidFrom(),
                                caDto.getCertificateDto().getValidityInMonths())
                );
            }

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
            return new CADto(ca);
        } else {
            throw new ApiBadRequestException("Root CA already exists.");
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

        CertificateExtension bcExtension = new CertificateExtension();
        bcExtension.setName("Basic Constraint");
        //bcExtension.setCertificate(certificate);
        bcExtension.getAttributes().add(
                new ExtensionAttribute(null, "Is Root Certificate Authority."));

        CertificateExtension keyUsageExtension = new CertificateExtension();
        keyUsageExtension.setName("Key Usage");
        //keyUsageExtension.setCertificate(certificate);
        keyUsageExtension.getAttributes().add(
                new ExtensionAttribute(null, "KeyCertSign"));
        keyUsageExtension.getAttributes().add(
                new ExtensionAttribute(null, "CrlSign"));

        certificate.getExtensions().add(bcExtension);
        certificate.getExtensions().add(keyUsageExtension);
        return certificate;
    }
}
