package bsep.pki.PublicKeyInfrastructure.dto;

import bsep.pki.PublicKeyInfrastructure.model.CertificateRequestStatus;
import bsep.pki.PublicKeyInfrastructure.model.CertificateRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@Getter
@Setter
public class CertificateRequestDto {

    Long id;

    @NotBlank
    private String commonName;

    @NotBlank
    private String organisation;

    @NotBlank
    private String organisationUnit;

    @NotBlank
    private String city;

    @NotBlank
    private String country;

    @NotBlank
    private String email;

    @NotBlank
    private String publicKey;

    private CertificateRequestStatus status;

    // TODO: add digital signature

    public CertificateRequestDto(CertificateRequest certRequest) {
        this.commonName = certRequest.getCommonName();
        this.organisation = certRequest.getOrganisation();
        this.organisationUnit = certRequest.getOrganisationUnit();
        this.city = certRequest.getCity();
        this.country = certRequest.getCountry();
        this.email = certRequest.getEmail();
        this.publicKey = certRequest.getPublicKey();
        this.status = certRequest.getStatus();
    }

}
