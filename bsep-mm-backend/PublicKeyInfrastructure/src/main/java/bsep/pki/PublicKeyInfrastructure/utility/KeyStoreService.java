package bsep.pki.PublicKeyInfrastructure.utility;

import bsep.pki.PublicKeyInfrastructure.exception.ApiInternalServerErrorException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

@Service
public class KeyStoreService {

    @Value("${keystore.name}")
    private String keyStoreName;

    @Value("${keystore.password}")
    private String keyStorePassword;

    @Value("${crl.path}")
    private String crlPublicPath;

    public void tryCreateKeyStore() {
        createKeystore();
    }

    public KeyStore createKeystore() {
        try {
            KeyStore keyStore = KeyStore.getInstance("JKS");
            keyStore.load(null, keyStorePassword.toCharArray());
            try (FileOutputStream fos = new FileOutputStream(keyStoreName)) {
                keyStore.store(fos, keyStorePassword.toCharArray());
            }
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

    public X509Certificate getSingleCertificate(String alias) {
        try {
            KeyStore keyStore = KeyStore.getInstance(
                    new File(keyStoreName),
                    keyStorePassword.toCharArray());
            Certificate certificate = keyStore.getCertificate(alias);
            return (X509Certificate) certificate;
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        }
        throw new ApiInternalServerErrorException("Error while getting single certificate from keystore");
    }

    public X509Certificate[] getCertificateChain(String alias) {
        try {
            KeyStore keyStore = KeyStore.getInstance(
                    new File(keyStoreName),
                    keyStorePassword.toCharArray());
            Certificate[] certificates = keyStore.getCertificateChain(alias);
            X509Certificate[] x509Certificates = new X509Certificate[certificates.length];
            for (int i = 0; i < certificates.length; i++)
                x509Certificates[i] = (X509Certificate) certificates[i];
            return x509Certificates;
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        }
        throw new ApiInternalServerErrorException("Error while getting single certificate from keystore");
    }

    public Key getKey(String alias) {
        try {
            KeyStore keyStore = KeyStore.getInstance(
                    new File(keyStoreName),
                    keyStorePassword.toCharArray());
            return keyStore.getKey(alias, keyStorePassword.toCharArray());
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        }
        throw new ApiInternalServerErrorException("Error while getting key from keystore");
    }

    public InputStream getPkcs12InputStream(X509Certificate[] chain, PrivateKey privateKey, String alias) {
        try {
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            keyStore.load(null, keyStorePassword.toCharArray());
            keyStore.setKeyEntry(
                    chain[0].getSubjectX500Principal().getName(),
                    privateKey,
                    keyStorePassword.toCharArray(),
                    chain);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            keyStore.store(out, keyStorePassword.toCharArray());
            return new ByteArrayInputStream(out.toByteArray());
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new ApiInternalServerErrorException("Something went wrong while generating PKCS12 file.");
    }

    public void saveEntry(X509Certificate[] chain, PrivateKey privateKey, String alias) {
        try {
            KeyStore keyStore = KeyStore.getInstance(new File(keyStoreName), keyStorePassword.toCharArray());
            keyStore.setKeyEntry(alias, privateKey, keyStorePassword.toCharArray(), chain);
            try (FileOutputStream fos = new FileOutputStream(keyStoreName)) {
                keyStore.store(fos, keyStorePassword.toCharArray());
            }
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
