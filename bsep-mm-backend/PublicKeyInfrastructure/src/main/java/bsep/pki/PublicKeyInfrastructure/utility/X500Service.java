package bsep.pki.PublicKeyInfrastructure.utility;

import bsep.pki.PublicKeyInfrastructure.exception.ApiInternalServerErrorException;
import bsep.pki.PublicKeyInfrastructure.model.Certificate;
import bsep.pki.PublicKeyInfrastructure.repository.CertificateRepository;
import org.bouncycastle.asn1.x500.X500Name;
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
    private CertificateRepository certificateRepository;

    @Autowired
    private KeyStoreService keyStoreService;

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
}
