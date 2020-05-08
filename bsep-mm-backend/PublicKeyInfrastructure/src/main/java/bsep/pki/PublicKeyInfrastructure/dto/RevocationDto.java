package bsep.pki.PublicKeyInfrastructure.dto;

import bsep.pki.PublicKeyInfrastructure.model.CertificateRevocation;
import bsep.pki.PublicKeyInfrastructure.model.enums.RevokeReason;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RevocationDto {
    private Long id;

    @NotNull
    private Long certificateId;

    @NotBlank
    private String serialNumber;

    @NotNull
    private RevokeReason revokeReason;
    
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm", timezone = "Europe/Belgrade")
    private Date revocationDate;

    public RevocationDto(CertificateRevocation certificateRevocation) {
        super();
        id = certificateRevocation.getId();
        certificateId = certificateRevocation.getCertificate().getId();
        revokeReason = certificateRevocation.getRevokeReason();
        revocationDate = certificateRevocation.getCreatedAt();
    }
}
