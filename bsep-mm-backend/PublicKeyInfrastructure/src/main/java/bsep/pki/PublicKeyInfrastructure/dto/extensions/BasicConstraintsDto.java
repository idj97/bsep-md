package bsep.pki.PublicKeyInfrastructure.dto.extensions;

import bsep.pki.PublicKeyInfrastructure.model.CertificateExtension;
import bsep.pki.PublicKeyInfrastructure.model.ExtensionAttribute;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BasicConstraintsDto extends AbstractExtensionDto {
    private Boolean isCa;
    private Integer pathLength;

    @Override
    public Extension getBCExtension(Map<String, Object> params) {
        BasicConstraints basicConstraints;
        if (pathLength != null)  basicConstraints = new BasicConstraints(pathLength);
        else                     basicConstraints = new BasicConstraints(isCa);

        return new Extension(
                Extension.basicConstraints,
                isCritical,
                ASN1OctetString.getInstance(basicConstraints));
    }

    @Override
    public CertificateExtension getExtensionEntity(Map<String, Object> params) {
        CertificateExtension certificateExtension = new CertificateExtension();
        certificateExtension.setName("Basic Constraints");

        ExtensionAttribute extensionAttribute = new ExtensionAttribute();
        if (pathLength != null) extensionAttribute.setName("CA certificate, path length:" + pathLength);
        else if (isCa)          extensionAttribute.setName("CA certificate, path length: unlimited");
        else                    extensionAttribute.setName("Not CA.");

        certificateExtension.getAttributes().add(extensionAttribute);
        return certificateExtension;
    }
}
