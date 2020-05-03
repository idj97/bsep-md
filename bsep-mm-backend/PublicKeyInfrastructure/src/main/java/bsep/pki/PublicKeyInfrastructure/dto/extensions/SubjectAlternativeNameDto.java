package bsep.pki.PublicKeyInfrastructure.dto.extensions;

import bsep.pki.PublicKeyInfrastructure.model.CertificateExtension;
import bsep.pki.PublicKeyInfrastructure.model.ExtensionAttribute;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.GeneralName;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SubjectAlternativeNameDto extends AbstractExtensionDto {
    private List<String> dnsNames = new ArrayList<>();
    private List<String> ipAddresses = new ArrayList<>();

    @Override
    public Extension getBCExtension(Map<String, Object> params) {
        ASN1EncodableVector vector = new ASN1EncodableVector();
        dnsNames.stream().forEach(name -> vector.add(new GeneralName(GeneralName.dNSName, name)));
        ipAddresses.stream().forEach(address -> vector.add(new GeneralName(GeneralName.iPAddress, address)));
        DERSequence derSequence = new DERSequence(vector);

        return new Extension(
                Extension.subjectAlternativeName,
                isCritical,
                ASN1OctetString.getInstance(derSequence));
    }

    @Override
    public CertificateExtension getExtensionEntity(Map<String, Object> params) {
        CertificateExtension certificateExtension = new CertificateExtension();
        certificateExtension.setName("Subject alterntive name");

        dnsNames.stream()
                .forEach(dns -> certificateExtension.getAttributes()
                        .add(new ExtensionAttribute(null, "DNS:" + dns)));

        ipAddresses.stream()
                .forEach(address -> certificateExtension.getAttributes()
                        .add(new ExtensionAttribute(null, "IP:" + address)));

        return certificateExtension;
    }
}
