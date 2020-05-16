package bsep.pki.PublicKeyInfrastructure.dto;

import bsep.pki.PublicKeyInfrastructure.dto.extensions.AbstractExtensionDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateCertificateDto {
    // key and signature generation params
    @NotBlank private String keyGenerationAlgorithm;
    @NotNull  private Integer keySize;
    @NotBlank private String signatureAlgorithm;

    // validity period
    @NotBlank private String validFrom;
    @NotBlank private String validUntil;

    // certificate details
    @NotNull
    private Boolean selfSigned;

    @NotBlank
    private String serialNumber;

    @Valid
    private NameDto name;

    @Valid
    private List<AbstractExtensionDto> extensions;

    private String issuingCaSerialNumber;

    // if created as CSR
    private Long csrId;
}
