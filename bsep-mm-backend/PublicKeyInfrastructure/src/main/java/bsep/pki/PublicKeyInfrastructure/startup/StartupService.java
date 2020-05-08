package bsep.pki.PublicKeyInfrastructure.startup;

import bsep.pki.PublicKeyInfrastructure.dto.CreateCertificateDto;
import bsep.pki.PublicKeyInfrastructure.dto.NameDto;
import bsep.pki.PublicKeyInfrastructure.dto.extensions.*;
import bsep.pki.PublicKeyInfrastructure.service.*;
import bsep.pki.PublicKeyInfrastructure.utility.DateService;
import bsep.pki.PublicKeyInfrastructure.utility.KeyStoreService;
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
public class StartupService {

    @Autowired
    private KeyStoreService keystoreService;

    @Autowired
    private RootCAService rootCAService;

    @Autowired
    private CAService caService;

    @Autowired
    private CRLService crlService;

    @Value("${app.init}")
    private Boolean initApp;

    @Autowired
    private CertificateService certificateService;

    @Autowired
    private DateService dateService;

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
        //initOcsp();
        if (initApp) {
            //keystoreService.tryCreateKeyStore();
            //rootCAService.tryCreateRootCA();

            //caService.tryCreateCA(1L, CAType.SIEM_AGENT_ISSUER, CertificateType.SIEM_AGENT_ISSUER);
            //caService.tryCreateCA(1L, CAType.SIEM_CENTER_ISSUER, CertificateType.SIEM_CENTER_ISSUER);

            //crlService.createCRL();
            //crlService.revokeCertificate(new RevocationDto(null, 1L, RevokeReason.KEY_COMPROMISE, null));
            //crlService.revokeCertificate(new RevocationDto(null, 2L, RevokeReason.PRIVILEGE_WITHDRAWN, null));
        }
    }

    @Autowired
    private CertService certService;

    @Autowired
    private OcspService ocspService;

    public void createRootCert() {
        Security.addProvider(new BouncyCastleProvider());
        NameDto nameDto = new NameDto();
        nameDto.setCN("root");

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
        certService.create(createCertificateDto);
    }

    public void createOcspCert() {
        Security.addProvider(new BouncyCastleProvider());
        NameDto nameDto = new NameDto();
        nameDto.setCN("ocsp");

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
        certService.create(createCertificateDto);
    }

    public void createSslIssuer() {
        Security.addProvider(new BouncyCastleProvider());
        NameDto nameDto = new NameDto();
        nameDto.setCN("ssl-issuer");

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
        certService.create(createCertificateDto);
    }

    public void createSslServerCert() {
        Security.addProvider(new BouncyCastleProvider());
        NameDto nameDto = new NameDto();
        nameDto.setCN("ssl-server");
        nameDto.setDC("localhost");

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
        certService.create(createCertificateDto);
    }

    public void createSslClientCert() {
        Security.addProvider(new BouncyCastleProvider());
        NameDto nameDto = new NameDto();
        nameDto.setCN("ssl-client");
        nameDto.setDC("localhost");

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
        certService.create(createCertificateDto);
    }

    public void initOcsp() {
        createRootCert();
        createOcspCert();
        ocspService.setOcspSigner("2");
        createSslIssuer();
        createSslServerCert();
        createSslClientCert();
    }
}
