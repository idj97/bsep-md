package bsep.pki.PublicKeyInfrastructure.dto;

import bsep.pki.PublicKeyInfrastructure.model.Certificate;
import bsep.pki.PublicKeyInfrastructure.model.enums.CertificateType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CertificateDto {
	private Long id;
    private String commonName;
    private String serialNumber;
    private String organisationUnit;
    private String organisation;
    private String locality;
    private String state;
    private String country;
    private String domainComponent;
    private String email;
    private String givenName;
    private String surname;

    @JsonFormat(pattern = "dd-MM-yyyy", timezone = "Europe/Belgrade")
    private Date validFrom;

    @JsonFormat(pattern = "dd-MM-yyyy", timezone = "Europe/Belgrade")
    private Date validUntil;

    private int validityInMonths;
    private CertificateDto issuer;
    private CertificateType certificateType;
    private RevocationDto revocation;
    private List<ExtensionDto> extensionDtoList;

    public CertificateDto(Certificate certificate) {
        super();
        id = certificate.getId();
        commonName = certificate.getCommonName();
        givenName = certificate.getGivenName();
        surname = certificate.getGivenName();
        organisation = certificate.getOrganisation();
        organisationUnit = certificate.getOrganisationUnit();
        country = certificate.getCountry();
        email = certificate.getEmail();
        state = certificate.getState();
        locality = certificate.getLocality();
        domainComponent = certificate.getDomainComponent();
        validFrom = certificate.getValidFrom();
        validUntil = certificate.getValidUntil();
        serialNumber = certificate.getSerialNumber();
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
