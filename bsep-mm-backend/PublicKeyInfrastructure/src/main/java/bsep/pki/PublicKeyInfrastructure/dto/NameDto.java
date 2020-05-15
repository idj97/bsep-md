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
    private String commonName;         // common name
    private String serialNumber;       // serial number
    private String organisationUnit;   // organisation unit
    private String organisation;       // organisation name
    private String locality;           // locality
    private String state;              // state
    private String country;            // country
    private String domainComponent;    // domain component
    private String email;              // email
    private String givenName;          // given name
    private String surname;            // surname

    public X500Name getBCX500Name() {
        X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);

        if (serialNumber != null)         builder.addRDN(BCStyle.SERIALNUMBER, serialNumber);
        if (commonName != null)           builder.addRDN(BCStyle.CN, commonName);
        if (organisationUnit != null)     builder.addRDN(BCStyle.OU, organisationUnit);
        if (organisation != null)         builder.addRDN(BCStyle.O, organisation);
        if (locality != null)             builder.addRDN(BCStyle.L, locality);
        if (state != null)                builder.addRDN(BCStyle.ST, state);
        if (country != null)              builder.addRDN(BCStyle.C, country);
        if (domainComponent != null)      builder.addRDN(BCStyle.DC, domainComponent);
        if (email != null)                builder.addRDN(BCStyle.E, email);
        if (givenName != null)            builder.addRDN(BCStyle.GIVENNAME, givenName);
        if (surname != null)              builder.addRDN(BCStyle.SURNAME, surname);

        X500Name x500Name = builder.build();
        return x500Name;
    }
}
