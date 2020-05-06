package bsep.pki.PublicKeyInfrastructure.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bouncycastle.cert.X509CertificateHolder;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;

//TODO: DELETE
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class X509CertificateData {
    private X509Certificate[] x509CertificateChain;
    private PrivateKey privateKey;
    private String serialNumber;
}
