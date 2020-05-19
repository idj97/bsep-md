package bsep.sa.SiemAgent.util;

import java.io.File;
import java.io.IOException;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class KeyStoreUtil {

    public static KeyStore getKeyStore(String keyStoreName, String password) {
        try {
            KeyStore keyStore = KeyStore.getInstance(
                    new File(keyStoreName),
                    password.toCharArray());
            return keyStore;
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        }
        System.out.println("Error while reading keystore.");
        System.exit(0);
        return null;
    }

    public static X509Certificate getSingleCertificate(String keyStoreName, String alias, String password) {
        try {
            KeyStore keyStore = KeyStore.getInstance(
                    new File(keyStoreName),
                    password.toCharArray());
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
        System.out.println("Error while reading single certificate from keystore.");
        System.exit(0);
        return null;
    }

    public static X509Certificate[] getCertificateChain(String keyStoreName, String alias, String password) {
        try {
            KeyStore keyStore = KeyStore.getInstance(
                    new File(keyStoreName),
                    password.toCharArray());
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
        System.out.println("Error while reading certificate chain from keystore.");
        System.exit(0);
        return null;
    }

    public static Key getKey(String keyStoreName, String alias, String password) {
        try {
            KeyStore keyStore = KeyStore.getInstance(
                    new File(keyStoreName),
                    password.toCharArray());
            return keyStore.getKey(alias, password.toCharArray());
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
        System.out.println("Error while reading key from keystore.");
        System.exit(0);
        return null;
    }

    public static List<X509Certificate> getAllCertificates(String keyStoreName, String password) {
        try {
            KeyStore keyStore = KeyStore.getInstance(
                    new File(keyStoreName),
                    password.toCharArray());

            List<X509Certificate> x509Certificates = new ArrayList<>();
            for (Iterator<String> it = keyStore.aliases().asIterator(); it.hasNext(); ) {
                String a = it.next();
                x509Certificates.add((X509Certificate)keyStore.getCertificate(a));
            }

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
        System.out.println("Error while reading all certs from keystore.");
        System.exit(0);
        return null;
    }
}
