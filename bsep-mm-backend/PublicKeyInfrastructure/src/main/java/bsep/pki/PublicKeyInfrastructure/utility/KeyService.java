package bsep.pki.PublicKeyInfrastructure.utility;

import bsep.pki.PublicKeyInfrastructure.exception.ApiInternalServerErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.*;

@Service
public class KeyService {

    @Autowired
    private KeyStoreService keyStoreService;

    public KeyPair generateKeyPair(String keyPairGenerationAlgorithm, int keySize) {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance(keyPairGenerationAlgorithm);
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
            keyGen.initialize(keySize, random);
            return keyGen.generateKeyPair();
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            e.printStackTrace();
        }
        throw new ApiInternalServerErrorException("Error while generating keys.");
    }

}
