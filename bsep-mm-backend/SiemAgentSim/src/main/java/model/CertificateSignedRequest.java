package model;

public class CertificateSignedRequest {

    private String encodedCsr;
    private String signedCsr;

    public CertificateSignedRequest() {
    }

    public String getEncodedCsr() {
        return encodedCsr;
    }

    public void setEncodedCsr(String encodedCsr) {
        this.encodedCsr = encodedCsr;
    }

    public String getSignedCsr() {
        return signedCsr;
    }

    public void setSignedCsr(String signedCsr) {
        this.signedCsr = signedCsr;
    }

    @Override
    public String toString() {
        return "{" +
                "\"encodedCsr\":\"" + encodedCsr + "\"" +
                ",\"signedCsr\":\"" + signedCsr + "\"" +
                "}";
    }
}
