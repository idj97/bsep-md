package service;

import model.CertificateRequest;
import model.CertificateSignedRequest;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

public class CSRService {

    private InputService inputSvc;
    private CryptoService cryptoSvc;
    private Base64Service base64Svc;

    public CSRService() {
        this.inputSvc = new InputService();
        this.cryptoSvc = new CryptoService();
        this.base64Svc = new Base64Service();
    }

    public CertificateRequest gatherRequestData() {

        System.out.println("In order to request a certificate, the following data is required: ");
        CertificateRequest request = new CertificateRequest();

        request.setCommonName(inputSvc.getUserInput("Enter common name:"));
        request.setGivenName(inputSvc.getUserInput("Enter given name:"));
        request.setSurname(inputSvc.getUserInput("Enter surname:"));
        request.setOrganisation(inputSvc.getUserInput("Enter organisation:"));
        request.setOrganisationUnit(inputSvc.getUserInput("Enter organisation unit:"));
        request.setCity(inputSvc.getUserInput("Enter city: "));
        request.setCountry(inputSvc.getUserInput("Enter country: "));
        request.setEmail(inputSvc.getUserInput("Enter email: "));

        return request;
    }

    public KeyPair generateKeys() {
        return cryptoSvc.generateKeyPair();
    }

    public String convertKeyToString(PublicKey key) {
        return base64Svc.encodePublicKey(key);
    }

    public CertificateSignedRequest createSignedRequest(CertificateRequest requestData, PrivateKey key) {

        CertificateSignedRequest signedRequest = new CertificateSignedRequest();

        signedRequest.setEncodedCsr(
                        base64Svc.encodeText(requestData.toString())
        );

        signedRequest.setSignedCsr(
                base64Svc.encodeBytes(
                        cryptoSvc.sign(signedRequest.getEncodedCsr().getBytes(), key)
                )
        );

        return signedRequest;
    }

}
