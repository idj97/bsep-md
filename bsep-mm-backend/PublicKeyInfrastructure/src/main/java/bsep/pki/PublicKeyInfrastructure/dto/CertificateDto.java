package bsep.pki.PublicKeyInfrastructure.dto;

import bsep.pki.PublicKeyInfrastructure.model.Certificate;
import bsep.pki.PublicKeyInfrastructure.model.CertificateType;
import com.fasterxml.jackson.annotation.JsonFormat;
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
public class    CertificateDto {
    @NotBlank
    private String commonName;

    @NotBlank
    private String givenName;

    @NotBlank
    private String surname;

    @NotBlank
    private String organisation;

    @NotBlank
    private String organisationUnit;

    @NotBlank
    private String country;

    @NotBlank
    private String email;

    @NotNull
    @Future
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm", timezone = "Europe/Belgrade")
    private Date validFrom;

    @NotNull
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm", timezone = "Europe/Belgrade")
    private Date validUntil;

    private Integer serialNumber;

    private CertificateType certificateType = CertificateType.UNDEFINED;

    private RevocationDto revocation;

    public CertificateDto(Certificate certificate) {
        super();
        commonName = certificate.getCN();
        givenName = certificate.getGivenName();
        surname = certificate.getGivenName();
        organisation = certificate.getO();
        organisationUnit = certificate.getOU();
        country = certificate.getC();
        email = certificate.getUserEmail();
        validFrom = certificate.getValidFrom();
        validUntil = certificate.getValidUntil();
        serialNumber = Integer.parseInt(certificate.getSerialNumber());
        if (certificate.getRevocation() != null)
            this.revocation = new RevocationDto(certificate.getRevocation());
    }
}
