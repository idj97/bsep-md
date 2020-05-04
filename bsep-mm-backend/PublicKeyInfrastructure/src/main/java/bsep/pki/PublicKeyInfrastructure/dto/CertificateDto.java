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
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CertificateDto {
	private Long id;
	
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

    private int validityInMonths;

    @JsonFormat(pattern = "dd-MM-yyyy HH:mm", timezone = "Europe/Belgrade")
    private Date validUntil;

    private CertificateDto issuer;
    private Integer serialNumber;
    private CertificateType certificateType;
    private RevocationDto revocation;
    private List<ExtensionDto> extensionDtoList;

    public CertificateDto(Certificate certificate) {
        super();
        id = certificate.getId();
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
        certificateType = certificate.getCertificateType();

        if (certificate.getRevocation() != null)
            this.revocation = new RevocationDto(certificate.getRevocation());

        extensionDtoList = certificate
                .getExtensions()
                .stream()
                .map(ExtensionDto::new)
                .collect(Collectors.toList());

        if (certificate.getIssuedByCertificate() != null)
            issuer = new CertificateDto(certificate.getIssuedByCertificate());
    }
}
