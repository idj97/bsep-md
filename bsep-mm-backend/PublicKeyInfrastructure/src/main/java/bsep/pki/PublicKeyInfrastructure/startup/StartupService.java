package bsep.pki.PublicKeyInfrastructure.startup;

import bsep.pki.PublicKeyInfrastructure.dto.CreateCertificateDto;
import bsep.pki.PublicKeyInfrastructure.dto.NameDto;
import bsep.pki.PublicKeyInfrastructure.dto.extensions.*;
import bsep.pki.PublicKeyInfrastructure.service.*;
import bsep.pki.PublicKeyInfrastructure.utility.DateService;
import bsep.pki.PublicKeyInfrastructure.utility.KeyStoreService;
import lombok.extern.apachecommons.CommonsLog;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.security.Security;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@CommonsLog
public class StartupService {

    @Autowired
    private KeyStoreService keystoreService;

    @Value("${app.init}")
    private Boolean initApp;

    @Autowired
    private CertificateService certificateService;

    @Autowired
    private DateService dateService;

    @Autowired
    private OcspService ocspService;


    @EventListener
    public void onStartup(ContextRefreshedEvent contextRefreshedEvent) {
        configure();
        initialize();
    }

    public void configure() {
        Security.addProvider(new BouncyCastleProvider());
    }

    // TODO: comment out
    public void initialize() {

        if (initApp) {
            log.info("Init app activated, creating keystore and ocsp certificates");
            keystoreService.tryCreateKeyStore();
            initCerts();
        }
    }

    public void initCerts() {
        createRootCert();
        createOcspCert();
        ocspService.setOcspSigner("2");
        createSslIssuer();
        createSiemCenterServerCert();
        createSiemAgentCert();
        createSiemAngularCert();
        createKeycloakCert();
    }

    public void createRootCert() {
        log.info("Creating root certificate");

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
        log.info("Creating OCSP certificate");

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

    public void createSslIssuer() {
        log.info("Creating SSL issuer certificate");

        Security.addProvider(new BouncyCastleProvider());
        NameDto nameDto = new NameDto();
        nameDto.setCommonName("ssl-issuer");

        KeyUsageDto keyUsageDto = new KeyUsageDto();
        keyUsageDto.setKeyCertSign(true);

        BasicConstraintsDto basicConstraintsDto = new BasicConstraintsDto(true, null);
        basicConstraintsDto.setIsCritical(true);


        List<AbstractExtensionDto> extensionDtos = new ArrayList<>() {
            {
                add(keyUsageDto);
                add(basicConstraintsDto);
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

    public void createSiemCenterServerCert() {

        log.info("Creating siem SSL server certificate");

        Security.addProvider(new BouncyCastleProvider());
        NameDto nameDto = new NameDto();
        nameDto.setCommonName("ssl-siem-server");
        nameDto.setDomainComponent("localhost");

        KeyUsageDto keyUsageDto = new KeyUsageDto();
        keyUsageDto.setDigitalSignature(true);
        keyUsageDto.setKeyEncipherment(true);

        ExtendedKeyUsageDto extendedKeyUsageDto = new ExtendedKeyUsageDto();
        extendedKeyUsageDto.setServerAuth(true);


        SubjectAlternativeNameDto sanDto = new SubjectAlternativeNameDto(
                Arrays.asList("localhost"),
                Arrays.asList("127.0.0.1")
        );

        List<AbstractExtensionDto> extensionDtos = new ArrayList<>() {
            {
                add(keyUsageDto);
                add(extendedKeyUsageDto);
                add(new AuthorityInfoAccessDto());
                add(new AuthorityKeyIdentifierDto());
                add(new SubjectKeyIdentifierDto());
                add(sanDto);
            }
        };

        CreateCertificateDto createCertificateDto = new CreateCertificateDto(
                "RSA",
                2048,
                "SHA256withRSA",
                "04-05-2020 00:00",
                "05-05-2021 00:00",
                false,
                "4",
                nameDto,
                extensionDtos,
                "3",
                null
        );
        certificateService.create(createCertificateDto);
    }

    public void createSiemAgentCert() {

        log.info("Creating siem agent SSL certificate");

        Security.addProvider(new BouncyCastleProvider());
        NameDto nameDto = new NameDto();
        nameDto.setCommonName("ssl-siem-agent");
        nameDto.setDomainComponent("localhost");

        KeyUsageDto keyUsageDto = new KeyUsageDto();
        keyUsageDto.setDigitalSignature(true);
        keyUsageDto.setKeyEncipherment(true);

        ExtendedKeyUsageDto extendedKeyUsageDto = new ExtendedKeyUsageDto();
        extendedKeyUsageDto.setClientAuth(true);

        SubjectAlternativeNameDto sanDto = new SubjectAlternativeNameDto(
                Arrays.asList("localhost"),
                Arrays.asList("127.0.0.1")
        );

        List<AbstractExtensionDto> extensionDtos = new ArrayList<>() {
            {
                add(keyUsageDto);
                add(extendedKeyUsageDto);
                add(new AuthorityInfoAccessDto());
                add(new AuthorityKeyIdentifierDto());
                add(new SubjectKeyIdentifierDto());
                add(sanDto);
            }
        };

        CreateCertificateDto createCertificateDto = new CreateCertificateDto(
                "RSA",
                2048,
                "SHA256withRSA",
                "04-05-2020 00:00",
                "05-05-2021 00:00",
                false,
                "5",
                nameDto,
                extensionDtos,
                "3",
                null
        );
        certificateService.create(createCertificateDto);
    }

    public void createSiemAngularCert() {

        log.info("Creating siem Angular SSL certificate");

        Security.addProvider(new BouncyCastleProvider());
        NameDto nameDto = new NameDto();
        nameDto.setCommonName("ssl-siem-angular");
        nameDto.setDomainComponent("localhost");

        KeyUsageDto keyUsageDto = new KeyUsageDto();
        keyUsageDto.setDigitalSignature(true);
        keyUsageDto.setKeyEncipherment(true);

        ExtendedKeyUsageDto extendedKeyUsageDto = new ExtendedKeyUsageDto();
        extendedKeyUsageDto.setServerAuth(true);


        SubjectAlternativeNameDto sanDto = new SubjectAlternativeNameDto(
                Arrays.asList("localhost"),
                Arrays.asList("127.0.0.1")
        );

        List<AbstractExtensionDto> extensionDtos = new ArrayList<>() {
            {
                add(keyUsageDto);
                add(extendedKeyUsageDto);
                add(new AuthorityInfoAccessDto());
                add(new AuthorityKeyIdentifierDto());
                add(new SubjectKeyIdentifierDto());
                add(sanDto);
            }
        };

        CreateCertificateDto createCertificateDto = new CreateCertificateDto(
                "RSA",
                2048,
                "SHA256withRSA",
                "04-05-2020 00:00",
                "05-05-2021 00:00",
                false,
                "6",
                nameDto,
                extensionDtos,
                "3",
                null
        );
        certificateService.create(createCertificateDto);
    }

    public void createKeycloakCert() {

        log.info("Creating Keycloak certificate");

        Security.addProvider(new BouncyCastleProvider());
        NameDto nameDto = new NameDto();
        nameDto.setCommonName("ssl-keycloak");
        nameDto.setDomainComponent("localhost");

        KeyUsageDto keyUsageDto = new KeyUsageDto();
        keyUsageDto.setDigitalSignature(true);
        keyUsageDto.setKeyEncipherment(true);

        ExtendedKeyUsageDto extendedKeyUsageDto = new ExtendedKeyUsageDto();
        extendedKeyUsageDto.setServerAuth(true);


        SubjectAlternativeNameDto sanDto = new SubjectAlternativeNameDto(
                Arrays.asList("localhost"),
                Arrays.asList("127.0.0.1")
        );

        List<AbstractExtensionDto> extensionDtos = new ArrayList<>() {
            {
                add(keyUsageDto);
                add(extendedKeyUsageDto);
                add(new AuthorityInfoAccessDto());
                add(new AuthorityKeyIdentifierDto());
                add(new SubjectKeyIdentifierDto());
                add(sanDto);
            }
        };

        CreateCertificateDto createCertificateDto = new CreateCertificateDto(
                "RSA",
                2048,
                "SHA256withRSA",
                "04-05-2020 00:00",
                "05-05-2021 00:00",
                false,
                "7",
                nameDto,
                extensionDtos,
                "3",
                null
        );
        certificateService.create(createCertificateDto);
    }
    
}
