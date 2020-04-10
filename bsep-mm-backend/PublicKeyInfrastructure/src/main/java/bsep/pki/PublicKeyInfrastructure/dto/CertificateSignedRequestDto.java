package bsep.pki.PublicKeyInfrastructure.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@Getter
@Setter
public class CertificateSignedRequestDto {

    @NotBlank
    private String encodedCsr;

    @NotBlank
    private String signedCsr;
}
