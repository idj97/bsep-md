package bsep.pki.PublicKeyInfrastructure.dto.extensions;

import bsep.pki.PublicKeyInfrastructure.model.CertificateExtension;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.Setter;
import org.bouncycastle.asn1.x509.Extension;

import java.io.IOException;
import java.util.Map;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @Type(value = BasicConstraintsDto.class,       name = "BASIC_CONSTRAINTS"),
        @Type(value = SubjectKeyIdentifierDto.class,   name = "SUBJECT_KEY_IDENTIFIER"),
        @Type(value = AuthorityKeyIdentifierDto.class, name = "AUTHORITY_KEY_IDENTIFIER"),
        @Type(value = KeyUsageDto.class,               name = "KEY_USAGE"),
        @Type(value = ExtendedKeyUsageDto.class,       name = "EXTENDED_KEY_USAGE"),
        @Type(value = AuthorityInfoAccessDto.class,    name = "AUTHORITY_INFO_ACCESS"),
        @Type(value = SubjectAlternativeNameDto.class, name = "SUBJECT_ALTERNATIVE_NAME")
})
@Getter
@Setter
public abstract class AbstractExtensionDto {
    protected Boolean isCritical = false;
    public abstract Extension getBCExtension(Map<String, Object> params) throws IOException;
    public abstract CertificateExtension getExtensionEntity(Map<String, Object> params);
}
