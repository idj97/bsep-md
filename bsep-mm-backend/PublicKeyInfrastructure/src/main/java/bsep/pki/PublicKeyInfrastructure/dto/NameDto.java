package bsep.pki.PublicKeyInfrastructure.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NameDto {
    @NotBlank
    private String serialNumber;

    @NotBlank
    private String CN;              // common name
    private String OU;              // organisation unit
    private String O;               // organisation name
    private String L;               // locality
    private String ST;              // state
    private String C;               // country
    private String DC;              // domain component
    private String E;               // email

    public X500Name getBCX500Name() {
        X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);

        if (serialNumber != null) builder.addRDN(BCStyle.SERIALNUMBER, serialNumber);
        if (CN != null)           builder.addRDN(BCStyle.CN, CN);
        if (CN != null)           builder.addRDN(BCStyle.CN, CN);
        if (OU != null)           builder.addRDN(BCStyle.OU, OU);
        if (O != null)            builder.addRDN(BCStyle.O, O);
        if (L != null)            builder.addRDN(BCStyle.L, L);
        if (ST != null)           builder.addRDN(BCStyle.ST, ST);
        if (C != null)            builder.addRDN(BCStyle.C, C);
        if (DC != null)           builder.addRDN(BCStyle.DC, DC);

        X500Name x500Name = builder.build();
        return x500Name;
    }
}
