package bsep.pki.PublicKeyInfrastructure.dto;

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
    private CAType CAType;

    private Long caIssuerId;
}
