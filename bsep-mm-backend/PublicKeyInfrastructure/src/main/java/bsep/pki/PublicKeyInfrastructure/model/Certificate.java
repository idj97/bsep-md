package bsep.pki.PublicKeyInfrastructure.model;

import bsep.pki.PublicKeyInfrastructure.dto.NameDto;
import bsep.pki.PublicKeyInfrastructure.model.enums.CertificateType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Certificate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String serialNumber;

    @OneToMany(mappedBy = "issuedByCertificate", cascade = CascadeType.ALL)
    private Set<Certificate> issuerForCertificates = new HashSet<>();

    @ManyToOne(cascade = CascadeType.ALL)
    private Certificate issuedByCertificate;

    @OneToOne(cascade = CascadeType.ALL)
    private CertificateRevocation revocation;

    @OneToOne(cascade = CascadeType.ALL)
    private CertificateRequest certificateRequest;

    @Column(unique = true, nullable = false)
    private String keyStoreAlias;

    @Column(nullable = false)
    private String keyGenerationAlgorithm;

    @Column(nullable = false)
    private Integer keySize;

    @Column(nullable = false)
    private String signingAlgorithm;

    @Column(nullable = false)
    private Boolean selfSigned;

    @Column(nullable = false)
    private Boolean isCa;
    private Integer pathLen;

    private String commonName;         // common name
    private String organisationUnit;   // organisation unit
    private String organisation;       // organisation name
    private String locality;           // locality
    private String state;              // state
    private String country;            // country
    private String domainComponent;    // domain component
    private String email;              // email
    private String givenName;          // given name
    private String surname;            // surname

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date validFrom;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date validUntil;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date dateCreated = new Date();

    @OneToMany(cascade = CascadeType.ALL)
    private List<CertificateExtension> extensions = new ArrayList<>();

    @Enumerated(value = EnumType.STRING)
    private CertificateType certificateType;

    @Column(nullable = false)
    private boolean isOcspResponder;

    public void setName(NameDto nameDto) {
        commonName = nameDto.getCommonName();
        organisationUnit = nameDto.getOrganisationUnit();
        organisation = nameDto.getOrganisation();
        locality = nameDto.getLocality();
        state = nameDto.getState();
        country = nameDto.getCountry();
        domainComponent = nameDto.getDomainComponent();
        email = nameDto.getEmail();
        givenName = nameDto.getGivenName();
        surname = nameDto.getSurname();
    }
}