package bsep.pki.PublicKeyInfrastructure.dto;

import bsep.pki.PublicKeyInfrastructure.model.CA;
import bsep.pki.PublicKeyInfrastructure.model.CAType;
import bsep.pki.PublicKeyInfrastructure.model.Certificate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CADto {

    private Long id;
    private Long caIssuerId;

    @NotNull
    private CAType caType;

    @NotNull
    @Valid
    private CertificateDto certificateDto;

    public CADto(CA ca) {
        id = ca.getId();
        if (ca.getParent() != null) caIssuerId = ca.getParent().getId();
        caType = ca.getType();
        certificateDto = new CertificateDto(ca.getCertificate());
    }

    public CADto(Certificate certificate) {
        id = certificate.getId();
        caIssuerId = certificate.getIssuedByCertificate().getIssuedForCA().getId();
        caType = null;
        certificateDto = new CertificateDto(certificate);
    }
}
