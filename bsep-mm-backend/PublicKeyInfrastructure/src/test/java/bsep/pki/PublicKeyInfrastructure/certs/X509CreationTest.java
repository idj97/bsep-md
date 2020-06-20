package bsep.pki.PublicKeyInfrastructure.certs;

import bsep.pki.PublicKeyInfrastructure.dto.CreateCertificateDto;
import bsep.pki.PublicKeyInfrastructure.dto.NameDto;
import bsep.pki.PublicKeyInfrastructure.dto.extensions.*;
import bsep.pki.PublicKeyInfrastructure.service.CertificateService;
import bsep.pki.PublicKeyInfrastructure.utility.KeyStoreService;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.security.Security;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test_h2")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class X509CreationTest {
    Date now = new Date();

    @Autowired
    private CertificateService certificateService;

    @Autowired
    private KeyStoreService keyStoreService;

    @Test
    public void testCreateRoot() {
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

    @Test
    public void testCreateInter() {
        testCreateRoot();
        Security.addProvider(new BouncyCastleProvider());
        NameDto nameDto = new NameDto();
        nameDto.setCommonName("inter");

        KeyUsageDto keyUsageDto = new KeyUsageDto();
        keyUsageDto.setKeyCertSign(true);
        keyUsageDto.setCRLSign(true);

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
                "04-05-2010 00:00",
                "05-05-2011 00:00",
                false,
                "2",
                nameDto,
                extensionDtos,
                "1",
                null
        );
        certificateService.create(createCertificateDto);
    }

    @Test
    public void testCreateEnd() {
        testCreateInter();
        Security.addProvider(new BouncyCastleProvider());
        NameDto nameDto = new NameDto();
        nameDto.setCommonName("end");

        KeyUsageDto keyUsageDto = new KeyUsageDto();
        keyUsageDto.setKeyEncipherment(true);
        keyUsageDto.setDigitalSignature(true);

        ExtendedKeyUsageDto extendedKeyUsageDto = new ExtendedKeyUsageDto();
        extendedKeyUsageDto.setServerAuth(true);

        List<AbstractExtensionDto> extensionDtos = new ArrayList<>() {
            {
                add(keyUsageDto);
                add(new SubjectKeyIdentifierDto());
                add(new AuthorityKeyIdentifierDto());
                add(extendedKeyUsageDto);
                add(new AuthorityInfoAccessDto());
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
                "2",
                null
        );
        certificateService.create(createCertificateDto);

        Assert.assertEquals(2, certificateService.getCaCertificates().size());

    }

    @Test
    public void testCreateEndInvalidDto() {
        testCreateInter();
        Security.addProvider(new BouncyCastleProvider());
        NameDto nameDto = new NameDto();
        nameDto.setCommonName("end");

        KeyUsageDto keyUsageDto = new KeyUsageDto();
        keyUsageDto.setKeyEncipherment(true);
        keyUsageDto.setDigitalSignature(true);

        ExtendedKeyUsageDto extendedKeyUsageDto = new ExtendedKeyUsageDto();
        extendedKeyUsageDto.setServerAuth(true);

        List<AbstractExtensionDto> extensionDtos = new ArrayList<>() {
            {
                add(keyUsageDto);
                add(new SubjectKeyIdentifierDto());
                add(new AuthorityKeyIdentifierDto());
                add(extendedKeyUsageDto);
                add(new AuthorityInfoAccessDto());
            }
        };

        // INVALID DATE
        CreateCertificateDto createCertificateDto = new CreateCertificateDto(
                "RSA",
                2048,
                "SHA256withRSA",
                "04-05-2020 00:00",
                "05-05-2019 00:00",
                false,
                "3",
                nameDto,
                extensionDtos,
                "2",
                null
        );

        try {
            certificateService.create(createCertificateDto);
            Assert.fail();
        } catch (Exception e) {
            Assert.assertTrue(true);
        }
        // wrong key gen algo
        createCertificateDto = new CreateCertificateDto(
                "RSAAA",
                2048,
                "SHA512WITHRSAENCRYPTION",
                "04-05-2020 00:00",
                "05-05-2022 00:00",
                false,
                "3",
                nameDto,
                extensionDtos,
                "2",
                null
        );
        try {
            certificateService.create(createCertificateDto);
            Assert.fail();
        } catch (Exception e) {
            Assert.assertTrue(true);

        }
        // wrong signign algo
        createCertificateDto = new CreateCertificateDto(
                "RSA",
                2048,
                "aaaaaaaaaa",
                "04-05-2020 00:00",
                "05-05-2022 00:00",
                false,
                "3",
                nameDto,
                extensionDtos,
                "2",
                null
        );
        try {
            certificateService.create(createCertificateDto);
            Assert.fail();
        } catch (Exception e) {
            Assert.assertTrue(true);

        }

        // self signed and issuer serial number
        createCertificateDto = new CreateCertificateDto(
                "RSA",
                2048,
                "aaaaaaaaaa",
                "04-05-2020 00:00",
                "05-05-2022 00:00",
                true,
                "3",
                nameDto,
                extensionDtos,
                "2",
                null
        );
        try {
            certificateService.create(createCertificateDto);
            Assert.fail();
        } catch (Exception e) {
            Assert.assertTrue(true);
        }
    }

}
