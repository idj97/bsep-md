package bsep.pki.PublicKeyInfrastructure.utility;

import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Service;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

@Service
public class SignatureService {

    public boolean verifySignature(byte[] data, byte[] signature, PublicKey publicKey) {

        try {
            Signature sig = Signature.getInstance("SHA1withRSA");
            sig.initVerify(publicKey);
            sig.update(data);
            return sig.verify(signature);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public PublicKey decodePublicKey(String encodedKeyStr) {

        try {
            byte[] publicKeyBin = Base64.decodeBase64(encodedKeyStr.getBytes());
            KeyFactory factory = KeyFactory.getInstance("RSA");
            return factory.generatePublic(new X509EncodedKeySpec(publicKeyBin));
        } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
            ex.printStackTrace();
        }

        return null;
    }
}
