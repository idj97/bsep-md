package bsep.pki.PublicKeyInfrastructure.dto.extensions;

import bsep.pki.PublicKeyInfrastructure.model.CertificateExtension;
import bsep.pki.PublicKeyInfrastructure.model.ExtensionAttribute;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bouncycastle.asn1.*;
import org.bouncycastle.asn1.x509.AccessDescription;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.GeneralName;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthorityInfoAccessDto extends AbstractExtensionDto {
    private List<String> ocspResponderUris = new ArrayList<>();
    private List<String> caIssuersUris = new ArrayList<>();

    @Override
    public Extension getBCExtension(Map<String, Object> params) {
        ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        ocspResponderUris.stream().forEach(uri -> getAccessDescription(AccessDescription.id_ad_ocsp, uri));
        caIssuersUris.stream().forEach(uri -> getAccessDescription(AccessDescription.id_ad_caIssuers, uri));

        DERSequence derSequence = new DERSequence(asn1EncodableVector);

        return new Extension(
                Extension.authorityInfoAccess,
                isCritical,
                ASN1OctetString.getInstance(derSequence));
    }

    @Override
    public CertificateExtension getExtensionEntity(Map<String, Object> params) {
        CertificateExtension certificateExtension = new CertificateExtension();
        certificateExtension.setName("Authority info access");

        ocspResponderUris.stream()
                .forEach(uri -> certificateExtension.getAttributes()
                        .add(new ExtensionAttribute(null, "OCSP: " + uri)));

        caIssuersUris.stream()
                .forEach(uri -> certificateExtension.getAttributes()
                        .add(new ExtensionAttribute(null, "CA: " + uri)));

        return certificateExtension;
    }

    private AccessDescription getAccessDescription(ASN1ObjectIdentifier type, String uri) {
        GeneralName generalName = new GeneralName(
                GeneralName.uniformResourceIdentifier,
                new DERIA5String(uri));

        return new AccessDescription(type, generalName);
    }
}
