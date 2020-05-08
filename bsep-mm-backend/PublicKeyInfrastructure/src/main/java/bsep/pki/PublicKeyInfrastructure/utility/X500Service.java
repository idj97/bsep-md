package bsep.pki.PublicKeyInfrastructure.utility;

import bsep.pki.PublicKeyInfrastructure.data.CreateX509CertificateData;
import bsep.pki.PublicKeyInfrastructure.data.IssuerData;
import bsep.pki.PublicKeyInfrastructure.data.SubjectData;
import bsep.pki.PublicKeyInfrastructure.data.X509CertificateData;
import bsep.pki.PublicKeyInfrastructure.dto.CertificateDto;
import bsep.pki.PublicKeyInfrastructure.exception.ApiInternalServerErrorException;
import bsep.pki.PublicKeyInfrastructure.model.Certificate;
import bsep.pki.PublicKeyInfrastructure.model.enums.CertificateType;
import bsep.pki.PublicKeyInfrastructure.repository.CertificateRepository;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class X500Service {

    @Autowired
    private CertificateGenerationService certificateGenerationService;

    @Autowired
    private CertificateRepository certificateRepository;

    @Autowired
    private KeyStoreService keyStoreService;

    //TODO: DELETE
    public X509CertificateData createRootCertificate(CertificateDto certificateDto) {
        KeyPair subjectKeyPair = generateKeyPair();
        Integer subjectSerialNumber = generateSerialNumber();
        certificateDto.setSerialNumber(subjectSerialNumber); // potrebno da bude u DTO prilikom kreiranja x500name

        return createX509Certificate(new CreateX509CertificateData(
                createX500NameFromCertificateDto(certificateDto),
                createX500NameFromCertificateDto(certificateDto),
                BigInteger.valueOf(subjectSerialNumber),
                null,
                subjectKeyPair,
                null,
                subjectKeyPair.getPrivate(),
                certificateDto.getValidFrom(),
                certificateDto.getValidUntil(),
                null,
                CertificateType.ROOT
        ));
    }

    //TODO: DELETE
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
                createX500NameFromCertificate(issuerCertificate),
                createX500NameFromCertificateDto(subjectCertificateDto),
                BigInteger.valueOf(subjectSerialNumber),
                new BigInteger(issuerCertificate.getSerialNumber()),
                subjectKeyPair,
                issuerCertificateData.getX509CertificateChain()[0].getPublicKey(),
                issuerCertificateData.getPrivateKey(),
                subjectCertificateDto.getValidFrom(),
                subjectCertificateDto.getValidUntil(),
                issuerCertificateData.getX509CertificateChain(),
                subjectCertificateDto.getCertificateType()
        ));
    }

    //TODO: DELETE
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
                data.getIssuerPublicKey(),
                data.getIssuerPrivateKey(),
                data.getIssuerX500Name(),
                data.getIssuerSerialNumber());

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

    //TODO: DELETE
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

    //TODO: DELETE
    public X500Name createX500NameFromCertificateDto(CertificateDto certificateDto) {
        X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);
        builder.addRDN(BCStyle.CN, certificateDto.getCommonName());
        builder.addRDN(BCStyle.SURNAME, certificateDto.getSurname());
        builder.addRDN(BCStyle.GIVENNAME, certificateDto.getGivenName());
        builder.addRDN(BCStyle.O, certificateDto.getOrganisation());
        builder.addRDN(BCStyle.OU, certificateDto.getOrganisationUnit());
        builder.addRDN(BCStyle.C, certificateDto.getCountry());
        builder.addRDN(BCStyle.E, certificateDto.getEmail());
        builder.addRDN(BCStyle.SERIALNUMBER, certificateDto.getSerialNumber().toString());
        X500Name subjectX500Name = builder.build();
        return subjectX500Name;
    }

    //TODO: DELETE
    private KeyPair generateKeyPair() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
            keyGen.initialize(2048, random);
            return keyGen.generateKeyPair();
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            e.printStackTrace();
        }
        return null;
    }

    public KeyPair generateKeyPair(String algorithm, int keySize) {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance(algorithm);
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
            keyGen.initialize(keySize, random);
            return keyGen.generateKeyPair();
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            e.printStackTrace();
        }
        throw new ApiInternalServerErrorException("Error while generating key pair");
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

    public X500Name getX500Name(String serialNumber) {
        try {
            X509Certificate x509Certificate = keyStoreService.getSingleCertificate(serialNumber);
            X509CertificateHolder x509Holder = new X509CertificateHolder(x509Certificate.getEncoded());
            return x509Holder.getSubject();
        } catch (CertificateEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new ApiInternalServerErrorException("Error while getting x500name");
    }

    public X509Certificate[] createX509CertChain(X509Certificate x509Certificate, String issuerSerialNumber) {
        if (issuerSerialNumber == null) {
            return new X509Certificate[] {x509Certificate};
        } else {
            X509Certificate[] issuerChain = keyStoreService.getCertificateChain(issuerSerialNumber);
            X509Certificate[] subjectChain = new X509Certificate[issuerChain.length+1];
            subjectChain[0] = x509Certificate;
            System.arraycopy(
                    issuerChain, 0,
                    subjectChain, 1,
                    issuerChain.length);
            return subjectChain;
        }
    }

    public X509Certificate generate(
            String signingAlgorithm,
            String subjectSerialNumber,
            X500Name subjectName,
            X500Name issuerName,
            Date validityStart,
            Date validityEnd,
            List<Extension> extensions,
            PublicKey subjectPublicKey,
            PrivateKey issuerPrivateKey
    ) {
        try {
            JcaContentSignerBuilder jcaContentSignerBuilder = new JcaContentSignerBuilder(signingAlgorithm);
            jcaContentSignerBuilder.setProvider("BC");
            ContentSigner contentSigner = jcaContentSignerBuilder.build(issuerPrivateKey);

            JcaX509v3CertificateBuilder x509v3CertificateBuilder = new JcaX509v3CertificateBuilder(
                    issuerName,
                    new BigInteger(subjectSerialNumber),
                    validityStart,
                    validityEnd,
                    subjectName,
                    subjectPublicKey
            );
            for (Extension e : extensions) x509v3CertificateBuilder.addExtension(e);

            X509CertificateHolder jcaX509CertificateHolder = x509v3CertificateBuilder.build(contentSigner);
            JcaX509CertificateConverter x509CertificateConverter = new JcaX509CertificateConverter();
            x509CertificateConverter = x509CertificateConverter.setProvider("BC");

            return x509CertificateConverter.getCertificate(jcaX509CertificateHolder);
        } catch (OperatorCreationException e) {
            e.printStackTrace();
        } catch (CertIOException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        }
        throw new ApiInternalServerErrorException();
    }

    //TODO: DELETE
    public void saveX509Certificate(X509CertificateData x509CertificateData) {
        keyStoreService.saveEntry(
                x509CertificateData.getX509CertificateChain(),
                x509CertificateData.getPrivateKey(),
                x509CertificateData.getSerialNumber()
        );
    }

}
