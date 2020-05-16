package bsep.pki.PublicKeyInfrastructure.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class X509CertificateWithKeys {
    X509Certificate x509Certificate;
    X509Certificate[] x509CertificatesChain;
    PublicKey publicKey;
    PrivateKey privateKey;
}
