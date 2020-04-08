package bsep.pki.PublicKeyInfrastructure.utility;

import bsep.pki.PublicKeyInfrastructure.data.CreateX509CertificateData;
import bsep.pki.PublicKeyInfrastructure.data.IssuerData;
import bsep.pki.PublicKeyInfrastructure.data.SubjectData;
import bsep.pki.PublicKeyInfrastructure.data.X509CertificateData;
import bsep.pki.PublicKeyInfrastructure.dto.CertificateDto;
import bsep.pki.PublicKeyInfrastructure.model.Certificate;
import bsep.pki.PublicKeyInfrastructure.model.CertificateType;
import bsep.pki.PublicKeyInfrastructure.repository.CertificateRepository;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x500.style.IETFUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.*;
import java.security.cert.X509Certificate;
import java.util.Optional;

@Service
public class X500Service {

    @Autowired
    private CertificateGenerationService certificateGenerationService;

    @Autowired
    private CertificateRepository certificateRepository;

    @Autowired
    private RootCertificateGenerationService rootCertificateGenerationService;

    @Autowired
    private KeyStoreService keyStoreService;

    public X509CertificateData createRootCertificate(CertificateDto certificateDto) {
        KeyPair subjectKeyPair = generateKeyPair();
        Integer subjectSerialNumber = generateSerialNumber();
        certificateDto.setSerialNumber(subjectSerialNumber); // potrebno da bude u DTO prilikom kreiranja x500name

        return createX509Certificate(new CreateX509CertificateData(
                createX500NameFromCertificateDto(certificateDto),
                createX500NameFromCertificateDto(certificateDto),
                subjectSerialNumber,
                subjectKeyPair,
                subjectKeyPair.getPrivate(),
                certificateDto.getValidFrom(),
                certificateDto.getValidUntil(),
                null,
                CertificateType.ROOT
        ));
    }

    public X509CertificateData createCertificate(
            CertificateDto subjectCertificateDto,
            Certificate issuerCertificate)
    {
        KeyPair subjectKeyPair = generateKeyPair();
        Integer subjectSerialNumber = generateSerialNumber();
        subjectCertificateDto.setSerialNumber(subjectSerialNumber); // potrebno da bude u DTO prilikom kreiranja x500name
        X509CertificateData issuerCertificateData = keyStoreService
                .getCaCertificate(issuerCertificate.getKeyStoreAlias());

        return createX509Certificate(new CreateX509CertificateData(
                createX500NameFromCertificateDto(subjectCertificateDto),
                createX500NameFromCertificate(issuerCertificate),
                subjectSerialNumber,
                subjectKeyPair,
                issuerCertificateData.getPrivateKey(),
                subjectCertificateDto.getValidFrom(),
                subjectCertificateDto.getValidUntil(),
                issuerCertificateData.getX509CertificateChain(),
                subjectCertificateDto.getCertificateType()
        ));
    }

    public X509CertificateData createX509Certificate(CreateX509CertificateData data)
    {
        // popuni subject podatke
        SubjectData subjectData = new SubjectData(
                data.getSubjectKeys().getPublic(),
                data.getSubjectX500Name(),
                data.getSubjectSerialNumber().toString(),
                data.getValidFrom(),
                data.getValidUntil());

        // popuni issuer podatke
        IssuerData issuerData = new IssuerData(
                data.getIssuerPrivateKey(),
                data.getIssuerX500Name());

        // kreiraj sertifikat
        X509Certificate subjectCertificate = certificateGenerationService.generate(
                subjectData,
                issuerData,
                data.getCertificateType());

        // napravi chain sertifikata za subject-a
        X509Certificate[] subjectChain;
        if (data.getCertificateType().equals(CertificateType.ROOT)) {
            subjectChain = new X509Certificate[1];
            subjectChain[0] = subjectCertificate;
        } else {
            X509Certificate[] issuerChain = data.getIssuerChain();
            subjectChain = new X509Certificate[issuerChain.length + 1];
            subjectChain[0] = subjectCertificate;
            System.arraycopy(
                    issuerChain, 0,
                    subjectChain, 1,
                    issuerChain.length);
        }

        return new X509CertificateData(
                subjectChain,
                data.getSubjectKeys().getPrivate(),
                data.getSubjectSerialNumber().toString());
    }

    public X500Name createX500NameFromCertificate(Certificate certificate) {
        X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);
        builder.addRDN(BCStyle.CN, certificate.getCN());
        builder.addRDN(BCStyle.SURNAME, certificate.getSurname());
        builder.addRDN(BCStyle.GIVENNAME, certificate.getGivenName());
        builder.addRDN(BCStyle.O, certificate.getO());
        builder.addRDN(BCStyle.OU, certificate.getOU());
        builder.addRDN(BCStyle.C, certificate.getC());
        builder.addRDN(BCStyle.E, certificate.getUserEmail());
        builder.addRDN(BCStyle.SERIALNUMBER, certificate.getSerialNumber());
        X500Name subjectX500Name = builder.build();
        return subjectX500Name;
    }

    public X500Name createX500NameFromCertificateDto(CertificateDto certificateDto) {
        X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);
        builder.addRDN(BCStyle.CN, certificateDto.getCommonName());
        builder.addRDN(BCStyle.SURNAME, certificateDto.getSurname());
        builder.addRDN(BCStyle.GIVENNAME, certificateDto.getGivenName());
        builder.addRDN(BCStyle.O, certificateDto.getOrganisation());
        builder.addRDN(BCStyle.OU, certificateDto.getOrganisation());
        builder.addRDN(BCStyle.C, certificateDto.getCountry());
        builder.addRDN(BCStyle.E, certificateDto.getEmail());
        builder.addRDN(BCStyle.SERIALNUMBER, certificateDto.getSerialNumber().toString());
        X500Name subjectX500Name = builder.build();
        return subjectX500Name;
    }


    public X509CertificateData createX509RootCertificateFromCertificateDto(CertificateDto certificateDto) {
        KeyPair keyPair = generateKeyPair();
        X500Name subjectX500Name = createX500NameFromCertificateDto(certificateDto);
        X500Name issuerX500Name = subjectX500Name;
        String serialNumber = getRdnValueFromX500Name(BCStyle.SERIALNUMBER, subjectX500Name);

        SubjectData subjectData = new SubjectData(
                keyPair.getPublic(),
                subjectX500Name,
                serialNumber,
                certificateDto.getValidFrom(),
                certificateDto.getValidUntil());
        IssuerData issuerData = new IssuerData(keyPair.getPrivate(), issuerX500Name);

        X509Certificate x509Certificate = rootCertificateGenerationService.generate(subjectData, issuerData);
        X509Certificate[] chain = {x509Certificate};

        keyStoreService.saveEntry(chain, keyPair.getPrivate(), serialNumber);
        return new X509CertificateData(chain, keyPair.getPrivate(), serialNumber);
    }

    public String getRdnValueFromX500Name(ASN1ObjectIdentifier identifier, X500Name x500Name) {
        RDN[] rdns = x500Name.getRDNs(identifier);
        return IETFUtils.valueToString(rdns[0].getFirst().getValue());
    }

    private KeyPair generateKeyPair() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
            keyGen.initialize(2048, random);
            return keyGen.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Integer generateSerialNumber() {
        SecureRandom secureRandom = new SecureRandom();
        while (true) {
            Integer serialNumber = secureRandom.nextInt(Integer.MAX_VALUE);
            Optional<Certificate> cert = certificateRepository.findBySerialNumber(serialNumber.toString());
            if (!cert.isPresent()) {
                return serialNumber;
            }
        }
    }

    public void saveX509Certificate(X509CertificateData x509CertificateData) {
        keyStoreService.saveEntry(
                x509CertificateData.getX509CertificateChain(),
                x509CertificateData.getPrivateKey(),
                x509CertificateData.getSerialNumber()
        );
    }

}
