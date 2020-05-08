package bsep.pki.PublicKeyInfrastructure.model;

import bsep.pki.PublicKeyInfrastructure.dto.RevocationDto;
import bsep.pki.PublicKeyInfrastructure.model.enums.RevokeReason;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CertificateRevocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "revocation", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Certificate certificate;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();

    @Enumerated(value = EnumType.STRING)
    private RevokeReason revokeReason;

    public CertificateRevocation(Certificate cert, RevokeReason revokeReason) {
        super();
        this.certificate = cert;
        this.revokeReason = revokeReason;
    }
}
