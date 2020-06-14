package bsep.pki.PublicKeyInfrastructure.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(propagation = Propagation.REQUIRED)
public class CertificateRequestService {

//    @Autowired
//    private CertificateRequestRepository certReqRepo;
//
//    @Autowired
//    X500Service x500Svc;
//
//    @Autowired
//    SignatureService signatureSvc;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @Autowired
//    private KeyStoreService keyStoreService;
//
//    @Autowired
//    private CertificateService certificateService;
//
//    public List<CertificateRequestDto> findByStatus(CertificateRequestStatus status) {
//        return certReqRepo
//                .findAllByStatus(status)
//                .stream()
//                .map(csr -> new CertificateRequestDto(csr))
//                .collect(Collectors.toList());
//    }
//
//    public Integer createCertificateSignRequest(CertificateSignedRequestDto certificateReq)  {
//
//        // deserialize json into dto
//        String certificateReqStr = new String(Base64.decodeBase64(certificateReq.getEncodedCsr().getBytes()));
//        CertificateRequest request = null;
//        try {
//            request = objectMapper.readValue(certificateReqStr, CertificateRequest.class);
//        } catch(IOException ex) {
//            throw new ApiBadRequestException("The submitted data format is invalid");
//        }
//
//        // check if the company name already exists
//        if(certReqRepo.findOneByCommonName(request.getCommonName()).isPresent()) {
//            throw new ApiBadRequestException("The submitted common name already exists");
//        }
//
//        // decode public key string and parse it
//        PublicKey key = signatureSvc.decodePublicKey(request.getPublicKey());
//        if(key == null) {
//            throw new ApiBadRequestException("The public key is in an invalid format");
//        }
//
//        // verify signature
//        if(!signatureSvc.verifySignature(
//                        certificateReq.getEncodedCsr().getBytes(),
//                        Base64.decodeBase64(certificateReq.getSignedCsr().getBytes()),
//                        key)) {
//            throw new ApiBadRequestException("The digital signature is invalid. Please resubmit.");
//        }
//
//        request.setSerialNumber(x500Svc.generateSerialNumber());
//        request.setCertificateType(CertificateType.SIEM_AGENT);
//
//        // save certificate request
//        request.setStatus(CertificateRequestStatus.PENDING);
//        certReqRepo.save(request);
//
//        return request.getSerialNumber();
//
//    }
//
//    public CertificateRequestDto approveCertificateSignRequest(Long certRequestId, Long issuerId) {
//
//        CertificateRequest request = certReqRepo
//                .findById(certRequestId)
//                .orElseThrow(() -> new ApiNotFoundException("Certificate not found"));
//
//        if(request.getStatus().equals(CertificateRequestStatus.APPROVED)) {
//            throw new ApiBadRequestException("The certificate has already been approved");
//        }
//
//        request.setStatus(CertificateRequestStatus.APPROVED);
//        certificateService.createCertificate(request, issuerId);
//        return new CertificateRequestDto(certReqRepo.save(request));
//    }
//
//    public CertificateRequestDto declineCertificateSignRequest(Long id) {
//        CertificateRequest request = certReqRepo
//                .findById(id)
//                .orElseThrow(() -> new ApiNotFoundException("Certificate not found"));
//
//        if(request.getStatus().equals(CertificateRequestStatus.DENIED)) {
//            throw new ApiBadRequestException("The certificate has already been denied");
//        }
//
//        request.setStatus(CertificateRequestStatus.DENIED);
//        return new CertificateRequestDto(certReqRepo.save(request));
//    }
//
//    public InputStreamResource getCertFileForCertRequest(long certificateRequestId) {
//    	Optional<CertificateRequest> optCertReq = certReqRepo.findById(certificateRequestId);
//    	if (!optCertReq.isPresent())
//    		throw new ApiException("No such certificate request", HttpStatus.NOT_FOUND);
//
//    	CertificateRequest certReq = optCertReq.get();
//    	X509CertificateData certData = keyStoreService.getCaCertificate(
//    	        certReq.getSerialNumber().toString());
//
//    	byte[] binary = null;
//		try {
//			binary = certData.getX509CertificateChain()[0].getEncoded();
//		} catch (CertificateEncodingException e) {
//			e.printStackTrace();
//		}
//
//    	return new InputStreamResource(new ByteArrayInputStream(binary));
//    }
}
