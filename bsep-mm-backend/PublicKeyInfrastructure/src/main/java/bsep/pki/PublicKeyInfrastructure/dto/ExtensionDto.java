package bsep.pki.PublicKeyInfrastructure.dto;

import bsep.pki.PublicKeyInfrastructure.model.CertificateExtension;
import bsep.pki.PublicKeyInfrastructure.model.ExtensionAttribute;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExtensionDto {
    private String name;
    private List<String> attributes;

    public ExtensionDto(CertificateExtension extension) {
        super();
        name = extension.getName();
        attributes = extension
                .getAttributes()
                .stream()
                .map(ExtensionAttribute::getName)
                .collect(Collectors.toList());
    }
}
