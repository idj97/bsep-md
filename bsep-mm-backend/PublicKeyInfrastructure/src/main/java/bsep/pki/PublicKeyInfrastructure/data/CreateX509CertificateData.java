package bsep.pki.PublicKeyInfrastructure.data;

import bsep.pki.PublicKeyInfrastructure.model.CertificateType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bouncycastle.asn1.x500.X500Name;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateX509CertificateData {
    private X500Name issuerX500Name;
    private X500Name subjectX500Name;
    private Integer subjectSerialNumber;
    private KeyPair subjectKeys;
    private PrivateKey issuerPrivateKey;
    private Date validFrom;
    private Date validUntil;
    private X509Certificate[] issuerChain;
    private CertificateType certificateType;
}
