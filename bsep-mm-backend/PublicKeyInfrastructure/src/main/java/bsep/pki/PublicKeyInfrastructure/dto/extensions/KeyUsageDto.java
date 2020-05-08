package bsep.pki.PublicKeyInfrastructure.dto.extensions;

import bsep.pki.PublicKeyInfrastructure.model.CertificateExtension;
import bsep.pki.PublicKeyInfrastructure.model.ExtensionAttribute;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.KeyUsage;

import com.fasterxml.jackson.annotation.JsonTypeName;

import java.io.IOException;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeName("KEY_USAGE")
public class KeyUsageDto extends AbstractExtensionDto {
    private Boolean digitalSignature = false;
    private Boolean nonRepudiation = false;
    private Boolean keyEncipherment = false;
    private Boolean dataEncipherment = false;
    private Boolean keyAgreement = false;
    private Boolean keyCertSign = false;
    private Boolean cRLSign = false;
    private Boolean encipherOnly = false;
    private Boolean decipherOnly = false;

    @Override
    public Extension getBCExtension(Map<String, Object> params) throws IOException {
        int usages = 0;
        if (digitalSignature) usages = usages | KeyUsage.digitalSignature;
        if (nonRepudiation)   usages = usages | KeyUsage.nonRepudiation;
        if (keyEncipherment)  usages = usages | KeyUsage.keyEncipherment;
        if (dataEncipherment) usages = usages | KeyUsage.dataEncipherment;
        if (keyAgreement)     usages = usages | KeyUsage.keyAgreement;
        if (keyCertSign)      usages = usages | KeyUsage.keyCertSign;
        if (cRLSign)          usages = usages | KeyUsage.cRLSign;
        if (encipherOnly)     usages = usages | KeyUsage.encipherOnly;
        if (decipherOnly)     usages = usages | KeyUsage.decipherOnly;
        KeyUsage keyUsage = new KeyUsage(usages);

        return new Extension(
                Extension.keyUsage,
                isCritical,
                keyUsage.getEncoded());
    }

    @Override
    public CertificateExtension getExtensionEntity(Map<String, Object> params) {
        CertificateExtension certificateExtension = new CertificateExtension();
        certificateExtension.setName("Key usage");

        if (digitalSignature) certificateExtension.getAttributes().add(new ExtensionAttribute(null, "Digital signature"));
        if (nonRepudiation)   certificateExtension.getAttributes().add(new ExtensionAttribute(null, "Non repudation"));
        if (keyEncipherment)  certificateExtension.getAttributes().add(new ExtensionAttribute(null, "Key encipherment"));
        if (dataEncipherment) certificateExtension.getAttributes().add(new ExtensionAttribute(null, "Data encipherment"));
        if (keyAgreement)     certificateExtension.getAttributes().add(new ExtensionAttribute(null, "Key agreement"));
        if (keyCertSign)      certificateExtension.getAttributes().add(new ExtensionAttribute(null, "Key cert sign"));
        if (cRLSign)          certificateExtension.getAttributes().add(new ExtensionAttribute(null, "CRL sign"));
        if (encipherOnly)     certificateExtension.getAttributes().add(new ExtensionAttribute(null, "Encipher only"));
        if (decipherOnly)     certificateExtension.getAttributes().add(new ExtensionAttribute(null, "Decipher only"));

        return certificateExtension;
    }
}
