package bsep.pki.PublicKeyInfrastructure.utility;

import bsep.pki.PublicKeyInfrastructure.data.IssuerData;
import bsep.pki.PublicKeyInfrastructure.data.SubjectData;
import bsep.pki.PublicKeyInfrastructure.data.X509CertificateData;
import bsep.pki.PublicKeyInfrastructure.dto.CertificateDto;
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
import java.util.UUID;

@Service
public class X500Service {

    @Autowired
    private RootCertificateGenerationService rootCertificateGenerationService;

    @Autowired
    private KeyStoreService keyStoreService;

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

}
