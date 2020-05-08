package bsep.pki.PublicKeyInfrastructure.dto.extensions;

import bsep.pki.PublicKeyInfrastructure.model.CertificateExtension;
import bsep.pki.PublicKeyInfrastructure.model.ExtensionAttribute;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;

import com.fasterxml.jackson.annotation.JsonTypeName;

import java.io.IOException;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonTypeName("BASIC_CONSTRAINTS")
public class BasicConstraintsDto extends AbstractExtensionDto {
    private Boolean isCa;
    private String pathLength;

    @Override
    public Extension getBCExtension(Map<String, Object> params) throws IOException {
        BasicConstraints basicConstraints;
        if (isCa) {
            if (pathLength == null || pathLength.equals("")) {
                basicConstraints = new BasicConstraints(true);
            } else {
                basicConstraints = new BasicConstraints(Integer.parseInt(pathLength));
            }
        } else {
            basicConstraints = new BasicConstraints(false);
        }

        return new Extension(
                Extension.basicConstraints,
                isCritical,
                basicConstraints.getEncoded());
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
