package bsep.pki.PublicKeyInfrastructure.dto.extensions;

import bsep.pki.PublicKeyInfrastructure.model.CertificateExtension;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.bouncycastle.asn1.x509.ExtendedKeyUsage;
import org.bouncycastle.asn1.x509.Extension;

import java.util.Map;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @Type(value = BasicConstraintsDto.class,       name = "basicConstraints"),
        @Type(value = SubjectKeyIdentifierDto.class,   name = "subjectKeyIdentifier"),
        @Type(value = AuthorityKeyIdentifierDto.class, name = "authorityKeyIdentifier"),
        @Type(value = KeyUsageDto.class,               name = "keyUsage"),
        @Type(value = ExtendedKeyUsage.class,          name = "extendedKeyUsage"),
        @Type(value = AuthorityInfoAccessDto.class,    name = "authorityInfoAccess"),
        @Type(value = SubjectAlternativeNameDto.class, name = "subjectAlternativeName")
})
public abstract class AbstractExtensionDto {
    protected Boolean isCritical;
    public abstract Extension getBCExtension(Map<String, Object> params);
    public abstract CertificateExtension getExtensionEntity(Map<String, Object> params);
}
