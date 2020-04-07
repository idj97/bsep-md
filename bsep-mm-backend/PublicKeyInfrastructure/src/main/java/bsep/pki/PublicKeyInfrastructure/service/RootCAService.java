package bsep.pki.PublicKeyInfrastructure.service;

import bsep.pki.PublicKeyInfrastructure.data.X509CertificateData;
import bsep.pki.PublicKeyInfrastructure.dto.CADto;
import bsep.pki.PublicKeyInfrastructure.dto.CertificateDto;
import bsep.pki.PublicKeyInfrastructure.model.CA;
import bsep.pki.PublicKeyInfrastructure.model.CAType;
import bsep.pki.PublicKeyInfrastructure.model.Certificate;
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
import java.security.SecureRandom;
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

    @Value("${root.create}")
    private Boolean createRoot;

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
        if (createRoot) {


            CertificateDto certificateDto = new CertificateDto(
                    commonName,
                    givenName,
                    surname,
                    organisation,
                    orgnisationUnit,
                    country,
                    email,
                    new Date(),
                    new Date(),
                    getRandomSerialNumber(),
                    null);
            CADto caDto = new CADto(certificateDto, CAType.ROOT, null);
            createRootCA(caDto);
        }
    }

    public CA createRootCA(CADto caDto) {
        Optional<CA> optionalCA = caRepository.findByType(CAType.ROOT);
        if (!optionalCA.isPresent()) {
            CertificateDto certificateDto = caDto.getCertificateDto();

            // kreiranje x509 sertifikata sa snimanjem keystore
            X509CertificateData x509CertificateData = x500Service.createX509RootCertificateFromCertificateDto(certificateDto);

            // kreiranje entiteta sertifikat koji odgovara kreiranom x509 sertifikatu
            Certificate certificate = createSelfSignedSertificateEntity(certificateDto, x509CertificateData.getSerialNumber());

            // kreiranje entiteta CA i uvezivanje sa entitetom sertifikat
            CA ca = new CA();
            ca.setCertificate(certificate);
            certificate.setIssuedForCA(ca);
            ca.setType(caDto.getCAType());

            // usnimi u bazu
            return caRepository.save(ca);
        } else {
            System.out.println("ROOT CA ALREADY EXISTS");
        }
        return null;
    }

    public Certificate createSelfSignedSertificateEntity(CertificateDto certificateDto, String serialNumber) {
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
        return certificate;
    }

    public Integer getRandomSerialNumber() {
        SecureRandom secureRandom = new SecureRandom();
        while (true) {
            Integer serialNumber = secureRandom.nextInt();
            Optional<Certificate> cert = certificateRepository.findBySerialNumber(serialNumber.toString());
            if (!cert.isPresent()) {
                return serialNumber;
            }
        }
    }

}
