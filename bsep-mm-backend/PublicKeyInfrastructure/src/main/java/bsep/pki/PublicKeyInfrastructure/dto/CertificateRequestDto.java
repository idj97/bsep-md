package bsep.pki.PublicKeyInfrastructure.dto;

import bsep.pki.PublicKeyInfrastructure.model.enums.CertificateRequestStatus;
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
    private String givenName;

    @NotBlank
    private String surname;

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


    public CertificateRequestDto(CertificateRequest certRequest) {
        this.id = certRequest.getId();
        this.commonName = certRequest.getCommonName();
        this.surname = certRequest.getSurname();
        this.givenName = certRequest.getGivenName();
        this.organisation = certRequest.getOrganisation();
        this.organisationUnit = certRequest.getOrganisationUnit();
        this.city = certRequest.getCity();
        this.country = certRequest.getCountry();
        this.email = certRequest.getEmail();
        this.publicKey = certRequest.getPublicKey();
        this.status = certRequest.getStatus();
    }

}
