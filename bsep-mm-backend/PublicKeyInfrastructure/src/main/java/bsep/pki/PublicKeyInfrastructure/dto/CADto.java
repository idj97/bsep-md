package bsep.pki.PublicKeyInfrastructure.dto;

import bsep.pki.PublicKeyInfrastructure.model.CA;
import bsep.pki.PublicKeyInfrastructure.model.CAType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CADto {

    @NotNull
    @Valid
    private CertificateDto certificateDto;

    @NotNull
    private CAType caType;
    private Long caIssuerId;
    private Long id;

    public CADto(CA ca) {
        id = ca.getId();
        if (ca.getParent() != null) caIssuerId = ca.getParent().getId();
        caType = ca.getType();
        certificateDto = new CertificateDto(ca.getCertificate());
    }
}
