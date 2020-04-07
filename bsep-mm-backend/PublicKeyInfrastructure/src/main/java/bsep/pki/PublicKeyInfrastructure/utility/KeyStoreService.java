package bsep.pki.PublicKeyInfrastructure.utility;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

@Service
public class KeyStoreService {

    @Value("${keystore.name}")
    private String keyStoreName;

    @Value("${keystore.password}")
    private String keyStorePassword;

    @Value("${keystore.create}")
    private Boolean createKeyStore;

    private KeyStore keyStore;

    public void tryCreateKeyStore() {
        if (createKeyStore) {
            keyStore = createKeystore();
        }
    }

    public KeyStore createKeystore() {
        try {
            KeyStore keyStore = KeyStore.getInstance("JKS");
            keyStore.load(null, keyStorePassword.toCharArray());
            keyStore.store(new FileOutputStream(keyStoreName), keyStorePassword.toCharArray());
            System.out.printf("New keystore saved at %s\n", keyStoreName);
            return keyStore;
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void saveEntry(X509Certificate[] chain, PrivateKey privateKey, String alias) {
        try {
            KeyStore keyStore = KeyStore.getInstance(new File(keyStoreName), keyStorePassword.toCharArray());
            keyStore.setKeyEntry(alias, privateKey, keyStorePassword.toCharArray(), chain);
            keyStore.store(new FileOutputStream(keyStoreName), keyStorePassword.toCharArray());
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        }
    }

}
