package bsep.pki.PublicKeyInfrastructure.utility;

import bsep.pki.PublicKeyInfrastructure.model.enums.RevokeReason;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.*;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.X509CRLHolder;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v2CRLBuilder;
import org.bouncycastle.cert.bc.BcX509ExtensionUtils;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.PrivateKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Date;

//TODO: DELETE
@Service
public class X500CrlService {
    private static boolean CRITICAL = true;
    private static boolean NOT_CRITICAL = false;
    private static Date NOW = new Date();
    private BcX509ExtensionUtils extensionUtils = new BcX509ExtensionUtils();

    @Value("${crl.path}")
    private String crlFilePath;

    @Value("${crl.public.path}")
    private String crlPublicPath;

    public void createRevocationList(X509Certificate issuerX509Certificate, PrivateKey issuerPrivateKey) {
        try {
            X509CertificateHolder issuerX509CertHolder = new JcaX509CertificateHolder(issuerX509Certificate);
            X500Name issuerX500Name = issuerX509CertHolder.getSubject();

            // napravi crl builder
            X509v2CRLBuilder crlBuilder = new X509v2CRLBuilder(issuerX500Name, NOW);

            // do kada bi ovaj CRL trebalo da bude validan
            crlBuilder.setNextUpdate(NOW);

            // javni kljuc onoga ko je potpisao CRL
            crlBuilder.addExtension(
                    Extension.authorityKeyIdentifier,
                    NOT_CRITICAL,
                    extensionUtils.createAuthorityKeyIdentifier(issuerX509CertHolder));

            // napravi content signera koji ce potpisati CRL privatnim kljucem issuer-a
            JcaContentSignerBuilder builder = new JcaContentSignerBuilder("SHA256WithRSAEncryption");
            builder = builder.setProvider("BC");
            ContentSigner contentSigner = builder.build(issuerPrivateKey);

            // napravi x509 crl
            X509CRLHolder x509CRLHolder = crlBuilder.build(contentSigner);

            // snimi u .crl fajl
            try (FileOutputStream os = new FileOutputStream(crlFilePath)) {
                os.write(x509CRLHolder.getEncoded());
            }
        } catch (CertificateEncodingException e) {
            e.printStackTrace();
        } catch (CertIOException e) {
            e.printStackTrace();
        } catch (OperatorCreationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void revokeCertificate(
            BigInteger serialNumberToRevoke,
            RevokeReason reason,
            PrivateKey issuerPrivateKey) {
        try {
            // ucitaj .crl fajl u x509CrlHolder
            FileInputStream is = new FileInputStream(crlFilePath);
            X509CRLHolder crlHolder = new X509CRLHolder(is);

            // inicijalizuj crlBuilder sa x509CrlHolder-om
            X509v2CRLBuilder crlBuilder = new X509v2CRLBuilder(crlHolder);

            // dodaj serialNumber sertifikata koji se revoke-uje
            crlBuilder.addCRLEntry(
                    serialNumberToRevoke,
                    new Date(),
                    reason.getKey());

            // kreiraj ContentSigner-a sa privatnim kljucem root-a
            JcaContentSignerBuilder builder = new JcaContentSignerBuilder("SHA256WithRSAEncryption");
            builder = builder.setProvider("BC");
            ContentSigner contentSigner = builder.build(issuerPrivateKey);

            // kreiraj crl
            X509CRLHolder x509CRLHolder = crlBuilder.build(contentSigner);

            // usnimi u .crl fajl
            try (FileOutputStream os = new FileOutputStream(crlFilePath)) {
                os.write(x509CRLHolder.getEncoded());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (OperatorCreationException e) {
            e.printStackTrace();
        }
    }

    public CRLDistPoint getCRLDistPoint() {
        GeneralName generalName = new GeneralName(
                GeneralName.uniformResourceIdentifier,
                crlPublicPath);

        DistributionPointName distributionPointName = new DistributionPointName(
                new GeneralNames(generalName));

        DistributionPoint distributionPoint = new DistributionPoint(
                distributionPointName,
                null,
                null);

        DistributionPoint[] distributionPoints = new DistributionPoint[1];
        distributionPoints[0] = distributionPoint;

        return new CRLDistPoint(distributionPoints);
    }
}
