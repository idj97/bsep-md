package bsep.pki.PublicKeyInfrastructure.service;

import bsep.pki.PublicKeyInfrastructure.data.X509CertificateData;
import bsep.pki.PublicKeyInfrastructure.dto.CertificateRequestDto;
import bsep.pki.PublicKeyInfrastructure.dto.CertificateSignedRequestDto;
import bsep.pki.PublicKeyInfrastructure.exception.ApiBadRequestException;
import bsep.pki.PublicKeyInfrastructure.exception.ApiException;
import bsep.pki.PublicKeyInfrastructure.exception.ApiNotFoundException;
import bsep.pki.PublicKeyInfrastructure.model.CertificateRequestStatus;
import bsep.pki.PublicKeyInfrastructure.model.CertificateRequest;
import bsep.pki.PublicKeyInfrastructure.model.CertificateType;
import bsep.pki.PublicKeyInfrastructure.repository.CertificateRequestRepository;
import bsep.pki.PublicKeyInfrastructure.utility.KeyStoreService;
import bsep.pki.PublicKeyInfrastructure.utility.SignatureService;
import bsep.pki.PublicKeyInfrastructure.utility.X500Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.PublicKey;
import java.security.cert.CertificateEncodingException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(propagation = Propagation.REQUIRED)
public class CertificateRequestService {

    @Autowired
    private CertificateRequestRepository certReqRepo;

    @Autowired
    X500Service x500Svc;

    @Autowired
    SignatureService signatureSvc;

    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private KeyStoreService keyStoreService;

    @Autowired
    private CertificateService certificateService;

    public List<CertificateRequestDto> findByStatus(CertificateRequestStatus status) {
        return certReqRepo
                .findAllByStatus(status)
                .stream()
                .map(csr -> new CertificateRequestDto(csr))
                .collect(Collectors.toList());
    }

    public Integer createCertificateSignRequest(CertificateSignedRequestDto certificateReq)  {

        // deserialize json into dto
        String certificateReqStr = new String(Base64.decodeBase64(certificateReq.getEncodedCsr().getBytes()));
        CertificateRequest request = null;
        try {
            request = objectMapper.readValue(certificateReqStr, CertificateRequest.class);
        } catch(IOException ex) {
            throw new ApiBadRequestException("The submitted data format is invalid");
        }

        // check if the company name already exists
        if(certReqRepo.findOneByCommonName(request.getCommonName()).isPresent()) {
            throw new ApiBadRequestException("The submitted common name already exists");
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
        request.setCertificateType(CertificateType.SIEM_AGENT);

        // save certificate request
        request.setStatus(CertificateRequestStatus.PENDING);
        certReqRepo.save(request);

        return request.getSerialNumber();

    }

    public CertificateRequestDto approveCertificateSignRequest(Long id) {

        CertificateRequest request = certReqRepo
                .findById(id)
                .orElseThrow(() -> new ApiNotFoundException("Certificate not found"));

        if(request.getStatus().equals(CertificateRequestStatus.APPROVED)) {
            throw new ApiBadRequestException("The certificate has already been approved");
        }

        request.setStatus(CertificateRequestStatus.APPROVED);

        // TODO: create and memorize certificate
        certificateService.createCertificate(request);

        return new CertificateRequestDto(certReqRepo.save(request));
    }

    public CertificateRequestDto declineCertificateSignRequest(Long id) {

        CertificateRequest request = certReqRepo
                .findById(id)
                .orElseThrow(() -> new ApiNotFoundException("Certificate not found"));

        if(request.getStatus().equals(CertificateRequestStatus.DENIED)) {
            throw new ApiBadRequestException("The certificate has already been denied");
        }

        request.setStatus(CertificateRequestStatus.DENIED);
        return new CertificateRequestDto(certReqRepo.save(request));
    }
    
    public ResponseEntity<InputStreamResource> downloadCertificate(long certificateRequestId) {
    	Optional<CertificateRequest> optCertReq = certReqRepo.findById(certificateRequestId);
    	if (!optCertReq.isPresent()) 
    		throw new ApiException("No such certificate request", HttpStatus.NOT_FOUND);
    	
    	CertificateRequest certReq = optCertReq.get();

    	X509CertificateData certData = keyStoreService.getCaCertificate(
    	        certReq.getSerialNumber().toString());
    	
    	byte[] binary = null;
		try {
			binary = certData.getX509CertificateChain()[0].getEncoded();
		} catch (CertificateEncodingException e) {
			e.printStackTrace();
		}
    	
    	InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(binary));

        HttpHeaders headers = this.getDownloadHeaders();
        headers.setContentDispositionFormData("attachment", "ceritifacte.cer");
    	return new ResponseEntity<>(resource, headers, HttpStatus.OK);
    }
    
    public HttpHeaders getDownloadHeaders() {
    	HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");
    	return headers;
    }

}
