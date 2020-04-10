package bsep.pki.PublicKeyInfrastructure.dto;

import bsep.pki.PublicKeyInfrastructure.model.CAType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CertificateSearchDto {
    @NotNull
    private boolean revoked;
    private String commonName;
    private CAType caType;
    private Date validFrom;
    private Date validUntil;
    @NotNull
    private int page;
    @NotNull
    private int pageSize;
}
