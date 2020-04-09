package bsep.pki.PublicKeyInfrastructure.dto;

import bsep.pki.PublicKeyInfrastructure.model.CertificateRevocation;
import bsep.pki.PublicKeyInfrastructure.model.RevokeReason;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RevocationDto {
    private Long id;

    @NotNull
    private Long certificateId;

    @NotNull
    private RevokeReason revokeReason;

    private Date revocationDate;

    public RevocationDto(CertificateRevocation certificateRevocation) {
        super();
        id = certificateRevocation.getId();
        certificateId = certificateRevocation.getCertificate().getId();
        revokeReason = certificateRevocation.getRevokeReason();
        revocationDate = certificateRevocation.getCreatedAt();
    }
}
