package bsep.pki.PublicKeyInfrastructure.dto.extensions;

import bsep.pki.PublicKeyInfrastructure.exception.ApiInternalServerErrorException;
import bsep.pki.PublicKeyInfrastructure.model.CertificateExtension;
import bsep.pki.PublicKeyInfrastructure.model.ExtensionAttribute;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.SubjectKeyIdentifier;
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
@JsonTypeName("SUBJECT_KEY_IDENTIFIER")
public class SubjectKeyIdentifierDto extends AbstractExtensionDto {

    @Override
    public Extension getBCExtension(Map<String, Object> params) {
        try {
            JcaX509ExtensionUtils extensionUtils = new JcaX509ExtensionUtils();
            PublicKey subjectPublicKey = (PublicKey) params.get("subjectPublicKey");

            SubjectKeyIdentifier subjectKeyIdentifier = extensionUtils
                    .createSubjectKeyIdentifier(subjectPublicKey);

            return new Extension(
                    Extension.subjectKeyIdentifier,
                    isCritical,
                    subjectKeyIdentifier.getEncoded());

        } catch (NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
            throw new ApiInternalServerErrorException();
        }
    }

    @Override
    public CertificateExtension getExtensionEntity(Map<String, Object> params) {
        X509Certificate subjectCert = (X509Certificate) params.get("subjectX509Cert");

        CertificateExtension certificateExtension = new CertificateExtension();
        certificateExtension.setName("Subject key identifier");
        certificateExtension.getAttributes().add(
                new ExtensionAttribute(null, getSubjectKeyIdentifier(subjectCert)));

        return certificateExtension;
    }

    public String getSubjectKeyIdentifier(X509Certificate x509Certificate) {
        byte[] octets = x509Certificate.getExtensionValue(Extension.subjectKeyIdentifier.getId());
        SubjectKeyIdentifier subjectKeyIdentifier = SubjectKeyIdentifier.getInstance(octets);
        byte[] keyIdentifier = subjectKeyIdentifier.getKeyIdentifier();
        return Hex.toHexString(keyIdentifier);
    }
}
