package bsep.pki.PublicKeyInfrastructure.model;

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

    @OneToOne(cascade = CascadeType.ALL)
    private CA issuedForCA;

    @OneToMany(mappedBy = "issuedByCertificate", cascade = CascadeType.ALL)
    private Set<Certificate> issuerForCertificates = new HashSet<>();

    @ManyToOne(cascade = CascadeType.ALL)
    private Certificate issuedByCertificate;

    @OneToOne(cascade = CascadeType.ALL)
    private CertificateRevocation revocation;

    @OneToOne(cascade = CascadeType.ALL)
    private CertificateRequest certificateRequest;

    @Column(nullable = false, unique = true)
    private String serialNumber;

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

    @Column
    private String userId;

    @Column
    private String userEmail;

    @Column
    private String O;

    @Column
    private String C;

    @Column
    private String OU;

    @Column
    private String GivenName;

    @Column
    private String Surname;

    @Column
    private String CN;

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
}