package app;

import model.CertificateRequest;
import model.CertificateSignedRequest;
import service.CSRService;

import java.security.KeyPair;

public class Simulator {

    private CSRService csr;

    public Simulator() {
        this.csr = new CSRService();
    }

    public void simulate() {

        CertificateRequest request = csr.gatherRequestData();

        System.out.println("Generating keys....:");
        KeyPair pair = this.csr.generateKeys();
        request.setPublicKey(csr.convertKeyToString(pair.getPublic()));

        CertificateSignedRequest signed = csr.createSignedRequest(request, pair.getPrivate());


        System.out.println("\nYour signed request is:");
        System.out.println(signed.toString() + "\n");

    }
}
