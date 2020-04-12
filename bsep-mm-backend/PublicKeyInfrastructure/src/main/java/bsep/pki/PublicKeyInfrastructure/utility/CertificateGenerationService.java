package bsep.pki.PublicKeyInfrastructure.utility;

import bsep.pki.PublicKeyInfrastructure.data.IssuerData;
import bsep.pki.PublicKeyInfrastructure.data.SubjectData;
import bsep.pki.PublicKeyInfrastructure.exception.ApiBadRequestException;
import bsep.pki.PublicKeyInfrastructure.model.Certificate;
import bsep.pki.PublicKeyInfrastructure.model.CertificateType;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.DERIA5String;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.*;
import org.bouncycastle.cert.AttributeCertificateIssuer;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.hibernate.Criteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

@Service
public class CertificateGenerationService {

    private static boolean CRITICAL = true;
    private static boolean NOT_CRITICAL = false;

    @Autowired
    private X500CrlService x500CrlService;

    public X509Certificate generate(
            SubjectData subjectData,
            IssuerData issuerData,
            CertificateType certificateType)
    {
        try {
            //Posto klasa za generisanje sertifiakta ne moze da primi direktno privatni kljuc pravi se builder za objekat
            //Ovaj objekat sadrzi privatni kljuc izdavaoca sertifikata i koristiti se za potpisivanje sertifikata
            //Parametar koji se prosledjuje je algoritam koji se koristi za potpisivanje sertifiakta
            JcaContentSignerBuilder builder = new JcaContentSignerBuilder("SHA256WithRSAEncryption");
            //Takodje se navodi koji provider se koristi, u ovom slucaju Bouncy Castle
            builder = builder.setProvider("BC");

            //Formira se objekat koji ce sadrzati privatni kljuc i koji ce se koristiti za potpisivanje sertifikata
            ContentSigner contentSigner = builder.build(issuerData.getPrivateKey());

            //Postavljaju se podaci za generisanje sertifiakta
            X509v3CertificateBuilder certGen = new JcaX509v3CertificateBuilder(
                    issuerData.getX500name(),
                    new BigInteger(subjectData.getSerialNumber()),
                    subjectData.getStartDate(),
                    subjectData.getEndDate(),
                    subjectData.getX500name(),
                    subjectData.getPublicKey());
            
            //Authority Information Access
//            AccessDescription caIssuers = new AccessDescription(
//            		AccessDescription.id_ad_caIssuers,
//            		new GeneralName(GeneralName.uniformResourceIdentifier,
//            				new DERIA5String("URL")));
//
//        	ASN1EncodableVector aia_ASN = new ASN1EncodableVector();
//        	aia_ASN.add(caIssuers);
//
//        	certGen.addExtension(Extension.authorityInfoAccess, false, new DERSequence(aia_ASN));

            //Postavljaju se ekstenzije u zavisnosti od tipa/namene sertifikata
            if (certificateType.equals(CertificateType.ROOT)) {
                setRootExtensions(certGen);
            } else if (certificateType.equals(CertificateType.SIEM_AGENT_ISSUER)) {
                setCertIssuerExtensions(certGen);
            } else if (certificateType.equals(CertificateType.SIEM_CENTER_ISSUER)) {
                setCertIssuerExtensions(certGen);
            } else if (certificateType.equals(CertificateType.SIEM_AGENT)){
                setSiemAgentCertExtensions(certGen);
            } else if (certificateType.equals(CertificateType.SIEM_CENTER)) {
                setSiemCenterCertExtensions(certGen);
            } else {
                throw new ApiBadRequestException("Bad certificate type.");
            }

            //Generise se sertifikat
            X509CertificateHolder certHolder = certGen.build(contentSigner);

            //Builder generise sertifikat kao objekat klase X509CertificateHolder
            //Nakon toga je potrebno certHolder konvertovati u sertifikat, za sta se koristi certConverter
            JcaX509CertificateConverter certConverter = new JcaX509CertificateConverter();
            certConverter = certConverter.setProvider("BC");

            //Konvertuje objekat u sertifikat
            return certConverter.getCertificate(certHolder);
        } catch (CertificateEncodingException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (OperatorCreationException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } 
        return null;
    }

    public void setRootExtensions(X509v3CertificateBuilder certGen) {
        try {
            certGen.addExtension(
                    Extension.basicConstraints,
                    CRITICAL,
                    new BasicConstraints(true));

            int keyUsageBits = KeyUsage.keyCertSign | KeyUsage.cRLSign;
            certGen.addExtension(
                    Extension.keyUsage,
                    CRITICAL,
                    new KeyUsage(keyUsageBits));
        } catch (CertIOException e) {
            e.printStackTrace();
        }
    }

    public void setCertIssuerExtensions(X509v3CertificateBuilder certGen) {
        try {
            certGen.addExtension(
                    Extension.basicConstraints,
                    CRITICAL,
                    new BasicConstraints(true));

            int keyUsageBits = KeyUsage.keyCertSign;
            certGen.addExtension(
                    Extension.keyUsage,
                    CRITICAL,
                    new KeyUsage(keyUsageBits));

            certGen.addExtension(
                    Extension.cRLDistributionPoints,
                    NOT_CRITICAL,
                    x500CrlService.getCRLDistPoint());
        } catch (CertIOException e) {
            e.printStackTrace();
        }
    }

    public void setSiemAgentCertExtensions(X509v3CertificateBuilder certGen) {
        try {
            certGen.addExtension(
                    Extension.basicConstraints,
                    CRITICAL,
                    new BasicConstraints(false));

            int keyUsageBits = KeyUsage.keyEncipherment | KeyUsage.digitalSignature;
            certGen.addExtension(
                    Extension.keyUsage,
                    CRITICAL,
                    new KeyUsage(keyUsageBits));

            certGen.addExtension(
                    Extension.cRLDistributionPoints,
                    NOT_CRITICAL,
                    x500CrlService.getCRLDistPoint());
        } catch (CertIOException e) {
            e.printStackTrace();
        }
    }

    public void setSiemCenterCertExtensions(X509v3CertificateBuilder certGen) {
        try {
            certGen.addExtension(
                    Extension.basicConstraints,
                    CRITICAL,
                    new BasicConstraints(false));

            int keyUsageBits = KeyUsage.keyEncipherment;
            certGen.addExtension(
                    Extension.keyUsage,
                    CRITICAL,
                    new KeyUsage(keyUsageBits));

            certGen.addExtension(
                    Extension.cRLDistributionPoints,
                    NOT_CRITICAL,
                    x500CrlService.getCRLDistPoint());
        } catch (CertIOException e) {
            e.printStackTrace();
        }
    }

}
