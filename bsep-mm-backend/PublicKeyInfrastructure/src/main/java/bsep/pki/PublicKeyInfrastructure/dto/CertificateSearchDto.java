package bsep.pki.PublicKeyInfrastructure.dto;

import bsep.pki.PublicKeyInfrastructure.model.enums.CertificateType;
import com.fasterxml.jackson.annotation.JsonFormat;
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
    private int page;

    @NotNull
    private int pageSize;

    @NotNull
    private Boolean revoked;

    //@NotNull ODKOMENTARISATI NAKON STO SE getAll zameni simpleSearch-om.
    private Boolean isCa;

    private String commonName = "";
    private CertificateType certificateType;

    @JsonFormat(pattern = "dd-MM-yyyy HH:mm", timezone = "Europe/Belgrade")
    private Date validFrom;

    @JsonFormat(pattern = "dd-MM-yyyy HH:mm", timezone = "Europe/Belgrade")
    private Date validUntil;

}
