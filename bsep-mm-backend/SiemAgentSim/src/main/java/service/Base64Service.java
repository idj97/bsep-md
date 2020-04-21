package service;

import java.security.PublicKey;
import java.util.Base64;

public class Base64Service {

    public String encodeText(String text) {
        return Base64.getEncoder().encodeToString(text.getBytes());
    }

    public String encodeBytes(byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }

    public String decodeText(byte[] data) {
        return new String(Base64.getDecoder().decode(data));
    }

    public String encodePublicKey(PublicKey key) {
        byte[] byteKey = key.getEncoded();
        return Base64.getEncoder().encodeToString(byteKey);
    }

}
