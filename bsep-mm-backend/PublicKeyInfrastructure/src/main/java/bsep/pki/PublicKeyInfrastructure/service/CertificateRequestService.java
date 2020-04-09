package bsep.pki.PublicKeyInfrastructure.service;

import bsep.pki.PublicKeyInfrastructure.dto.CertificateRequestDto;
import bsep.pki.PublicKeyInfrastructure.dto.CertificateSignedRequestDto;
import bsep.pki.PublicKeyInfrastructure.exception.ApiBadRequestException;
import bsep.pki.PublicKeyInfrastructure.exception.ApiNotFoundException;
import bsep.pki.PublicKeyInfrastructure.model.CertificateRequestStatus;
import bsep.pki.PublicKeyInfrastructure.model.CertificateRequest;
import bsep.pki.PublicKeyInfrastructure.repository.CertificateRequestRepository;
import bsep.pki.PublicKeyInfrastructure.utility.SignatureService;
import bsep.pki.PublicKeyInfrastructure.utility.X500Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.PublicKey;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CertificateRequestService {

    @Autowired
    private CertificateRequestRepository certReqRepo;

    @Autowired
    X500Service x500Svc;

    @Autowired
    SignatureService signatureSvc;

    @Autowired
    private ObjectMapper objectMapper;


    public List<CertificateRequestDto> findByStatus(CertificateRequestStatus status) {
        return certReqRepo
                .findAllByStatus(status)
                .stream()
                .map(csr -> new CertificateRequestDto(csr))
                .collect(Collectors.toList());
    }

    public Integer createCertificateSignRequest(CertificateSignedRequestDto certificateReq)  {

        // parse json string into object
        String certificateReqStr = new String(Base64.decodeBase64(certificateReq.getEncodedCsr().getBytes()));
        CertificateRequest request = null;
        try {
            request = objectMapper.readValue(certificateReqStr, CertificateRequest.class);
        } catch(IOException ex) {
            throw new ApiBadRequestException("The submitted data format is invalid");
        }

        // decode public key string and parse it
        PublicKey key = signatureSvc.decodePublicKey(request.getPublicKey());
        if(key == null) {
            throw new ApiBadRequestException("The public key is in an invalid format");
        }

        // verify signature
        if(!signatureSvc.verifySignature(
                        certificateReq.getEncodedCsr().getBytes(),
                        Base64.decodeBase64(certificateReq.getSignedCsr().getBytes()),
                        key)) {
            throw new ApiBadRequestException("The digital signature is invalid. Please resubmit.");
        }

        request.setSerialNumber(x500Svc.generateSerialNumber());

        // save certificate request
        request.setStatus(CertificateRequestStatus.PENDING);
        certReqRepo.save(request);

        return request.getSerialNumber();

    }

    public CertificateRequestDto approveCertificateSignRequest(Long id) {

        CertificateRequest request = certReqRepo
                .findById(id)
                .orElseThrow(() -> new ApiNotFoundException("Certificate not found"));

        request.setStatus(CertificateRequestStatus.APPROVED);
        certReqRepo.save(request);

        // TODO: create and memorize certificate

        return new CertificateRequestDto(certReqRepo.save(request));
    }

    public CertificateRequestDto declineCertificateSignRequest(Long id) {

        CertificateRequest request = certReqRepo
                .findById(id)
                .orElseThrow(() -> new ApiNotFoundException("Certificate not found"));

        request.setStatus(CertificateRequestStatus.DENIED);
        return new CertificateRequestDto(certReqRepo.save(request));
    }

}
