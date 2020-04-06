package bsep.pki.PublicKeyInfrastructure.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CertificateDto {
    @NotBlank
    private String commonName;

    @NotBlank
    private String givenName;

    @NotBlank
    private String surname;

    @NotBlank
    private String organisation;

    @NotBlank
    private String orgnisationUnit;

    @NotBlank
    private String country;

    @NotBlank
    private String email;

    @NotNull
    @Future
    private Date validFrom;

    @NotNull
    @Future
    private Date validUntil;

    @NotNull
    private Integer serialNumber;

    private Long issuer;
}
