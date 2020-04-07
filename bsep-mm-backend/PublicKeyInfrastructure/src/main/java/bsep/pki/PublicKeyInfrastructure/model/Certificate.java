package bsep.pki.PublicKeyInfrastructure.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

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

    @ManyToOne(cascade = CascadeType.ALL)
    private CA issuedByCA;

    @OneToMany(mappedBy = "issuedByCertificate", cascade = CascadeType.ALL)
    private Set<Certificate> issuerForCertificates = new HashSet<>();

    @ManyToOne(cascade = CascadeType.ALL)
    private Certificate issuedByCertificate;

    @OneToOne(cascade = CascadeType.ALL)
    private CertificateRevocation revocation;

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
}