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

    @ManyToOne(cascade = CascadeType.ALL)
    private CertificateAuthority certificateAuthority;

    @OneToOne(cascade = CascadeType.ALL)
    private CertificateRevocation revocation;

    @OneToMany(mappedBy = "issuedBy", cascade = CascadeType.ALL)
    private Set<Certificate> issuerFor = new HashSet<>();

    @ManyToOne(cascade = CascadeType.ALL)
    private Certificate issuedBy;

    @Column(unique = true, nullable = false)
    private String keyStoreAlias;

    @Column(nullable = false)
    private Long userId;

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