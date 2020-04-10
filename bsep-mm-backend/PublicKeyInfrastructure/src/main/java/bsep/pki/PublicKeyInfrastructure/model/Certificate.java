package bsep.pki.PublicKeyInfrastructure.model;

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
    private String userId;

    @Column(nullable = false)
    private String userEmail;

    @Column(nullable = false)
    private String O;

    @Column(nullable = false)
    private String C;

    @Column(nullable = false)
    private String OU;

    @Column(nullable = false)
    private String GivenName;

    @Column(nullable = false)
    private String Surname;

    @Column(nullable = false)
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

    @OneToMany(mappedBy = "certificate", cascade = CascadeType.ALL)
    private List<Extension> extensions = new ArrayList<>();

    @Enumerated(value = EnumType.STRING)
    private CertificateType certificateType;
}