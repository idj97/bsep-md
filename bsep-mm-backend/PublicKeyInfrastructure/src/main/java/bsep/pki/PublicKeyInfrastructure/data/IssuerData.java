package bsep.pki.PublicKeyInfrastructure.data;

import bsep.pki.PublicKeyInfrastructure.model.Certificate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bouncycastle.asn1.x500.X500Name;

import java.math.BigInteger;
import java.security.PrivateKey;
import java.security.PublicKey;

//TODO: DELETE
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class IssuerData {
	private PublicKey publicKey;
	private PrivateKey privateKey;
	private X500Name x500name;
	private BigInteger serialNumber;
}
