package bsep.pki.PublicKeyInfrastructure.dto.extensions;

import bsep.pki.PublicKeyInfrastructure.exception.ApiInternalServerErrorException;
import bsep.pki.PublicKeyInfrastructure.model.CertificateExtension;
import bsep.pki.PublicKeyInfrastructure.model.ExtensionAttribute;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.x509.AuthorityKeyIdentifier;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import org.bouncycastle.util.encoders.Hex;

import com.fasterxml.jackson.annotation.JsonTypeName;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@JsonTypeName("AUTHORITY_KEY_IDENTIFIER")
public class AuthorityKeyIdentifierDto extends AbstractExtensionDto {

    @Override
    public Extension getBCExtension(Map<String, Object> params) {
        try {
            JcaX509ExtensionUtils extensionUtils = new JcaX509ExtensionUtils();
            PublicKey authorityPublicKey = (PublicKey) params.get("issuerPublicKey");

            AuthorityKeyIdentifier authorityKeyIdentifier = extensionUtils
                    .createAuthorityKeyIdentifier(authorityPublicKey);

            return new Extension(
                    Extension.authorityKeyIdentifier,
                    isCritical,
                    authorityKeyIdentifier.getEncoded());

        } catch (NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
            throw new ApiInternalServerErrorException();
        }
    }

    @Override
    public CertificateExtension getExtensionEntity(Map<String, Object> params) {
        X509Certificate subjectCert = (X509Certificate) params.get("subjectX509Cert");

        CertificateExtension certificateExtension = new CertificateExtension();
        certificateExtension.setName("Authority key identifier");
        certificateExtension.getAttributes().add(
                new ExtensionAttribute(null, getAuthorityKeyIdentifier(subjectCert)));

        return certificateExtension;
    }

    public String getAuthorityKeyIdentifier(X509Certificate x509Certificate) {
        byte[] octets = x509Certificate.getExtensionValue(Extension.authorityKeyIdentifier.getId());
        AuthorityKeyIdentifier authorityKeyIdentifier = new AuthorityKeyIdentifier(octets);
        byte[] keyIdentifier = authorityKeyIdentifier.getKeyIdentifier();
        return Hex.toHexString(keyIdentifier);
    }
}
