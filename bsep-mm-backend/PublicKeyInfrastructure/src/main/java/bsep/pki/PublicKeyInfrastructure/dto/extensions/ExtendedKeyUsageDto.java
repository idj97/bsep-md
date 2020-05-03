package bsep.pki.PublicKeyInfrastructure.dto.extensions;

import bsep.pki.PublicKeyInfrastructure.model.CertificateExtension;
import bsep.pki.PublicKeyInfrastructure.model.ExtensionAttribute;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.x509.ExtendedKeyUsage;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.KeyPurposeId;

import java.util.Map;
import java.util.Vector;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExtendedKeyUsageDto extends AbstractExtensionDto {
    private Boolean anyExtendedKeyUsage = false;
    private Boolean serverAuth = false;
    private Boolean clientAuth = false;
    private Boolean codeSigning = false;
    private Boolean emailProtection = false;
    private Boolean OCSPSigning = false;

    @Override
    public Extension getBCExtension(Map<String, Object> params) {
        Vector<KeyPurposeId> keyPurposeIdsVector = new Vector<>();

        if (anyExtendedKeyUsage) keyPurposeIdsVector.add(KeyPurposeId.anyExtendedKeyUsage);
        else {
            if (serverAuth)      keyPurposeIdsVector.add(KeyPurposeId.id_kp_serverAuth);
            if (clientAuth)      keyPurposeIdsVector.add(KeyPurposeId.id_kp_clientAuth);
            if (codeSigning)     keyPurposeIdsVector.add(KeyPurposeId.id_kp_codeSigning);
            if (emailProtection) keyPurposeIdsVector.add(KeyPurposeId.id_kp_emailProtection);
            if (OCSPSigning)     keyPurposeIdsVector.add(KeyPurposeId.id_kp_OCSPSigning);
        }
        KeyPurposeId[] keyPurposeIdsArray = (KeyPurposeId[]) keyPurposeIdsVector.toArray();
        ExtendedKeyUsage extendedKeyUsage = new ExtendedKeyUsage(keyPurposeIdsArray);

        return new Extension(
                Extension.extendedKeyUsage,
                isCritical,
                ASN1OctetString.getInstance(extendedKeyUsage));
    }

    @Override
    public CertificateExtension getExtensionEntity(Map<String, Object> params) {
        CertificateExtension certificateExtension = new CertificateExtension();
        certificateExtension.setName("Extended key usage");

        if (serverAuth)      certificateExtension.getAttributes().add(new ExtensionAttribute(null, "SSL server"));
        if (clientAuth)      certificateExtension.getAttributes().add(new ExtensionAttribute(null, "SSL client"));
        if (codeSigning)     certificateExtension.getAttributes().add(new ExtensionAttribute(null, "Code signing"));
        if (emailProtection) certificateExtension.getAttributes().add(new ExtensionAttribute(null, "Email protection"));
        if (OCSPSigning)     certificateExtension.getAttributes().add(new ExtensionAttribute(null, "OCSP signing"));

        return certificateExtension;
    }
}