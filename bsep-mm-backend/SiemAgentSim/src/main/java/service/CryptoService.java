package service;

import java.security.*;

public class CryptoService {

    public KeyPair generateKeyPair() {

        KeyPairGenerator keyGen = null;
        try {
            keyGen = KeyPairGenerator.getInstance("RSA");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
            keyGen.initialize(2048, random);
            return keyGen.generateKeyPair();
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            e.printStackTrace();
        }

        return null;
    }

    public byte[] sign(byte[] data, PrivateKey privateKey) {

        Signature sig = null;
        try {
            sig = Signature.getInstance("SHA1withRSA");
            sig.initSign(privateKey); // kojim kljucem potpisujemo
            sig.update(data); // koje podatke potpisujemo
            return sig.sign();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }

}
