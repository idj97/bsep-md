package bsep.pki.PublicKeyInfrastructure.model;

import bsep.pki.PublicKeyInfrastructure.dto.CertificateRequestDto;
import bsep.pki.PublicKeyInfrastructure.model.enums.CertificateRequestStatus;
import bsep.pki.PublicKeyInfrastructure.model.enums.CertificateType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CertificateRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer serialNumber;

    @Column(nullable=false)
    private String commonName;

    @Column(nullable = false)
    private String givenName;

    @Column(nullable = false)
    private String surname;

    @Column(nullable=false)
    private String organisation;

    @Column(nullable=false)
    private String organisationUnit;

    @Column(nullable=false)
    private String city;

    @Column(nullable=false)
    private String country;

    @Column(nullable=false)
    private String email;

    @Column(nullable=false, length = 500)
    private String publicKey;

    @Enumerated(value = EnumType.STRING)
    private CertificateType certificateType;

    @Column(nullable=false)
    private CertificateRequestStatus status;

    @OneToOne(cascade = CascadeType.ALL)
    private Certificate certificate;


    public CertificateRequest(CertificateRequestDto requestDto) {
        this.commonName = requestDto.getCommonName();
        this.givenName = requestDto.getGivenName();
        this.surname = requestDto.getSurname();
        this.organisation = requestDto.getOrganisation();
        this.organisationUnit = requestDto.getOrganisationUnit();
        this.city = requestDto.getCity();
        this.country = requestDto.getCountry();
        this.email = requestDto.getEmail();
        this.publicKey = requestDto.getPublicKey();
    }
}
