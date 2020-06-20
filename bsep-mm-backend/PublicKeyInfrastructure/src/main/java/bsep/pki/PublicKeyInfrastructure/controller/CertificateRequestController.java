package bsep.pki.PublicKeyInfrastructure.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/certificate-request")
public class CertificateRequestController {
//
//    @Autowired
//    CertificateRequestService certificateReqSvc;
//
//    @GetMapping
//    @PreAuthorize("hasRole('admin')")
//    public ResponseEntity<List<CertificateRequestDto>> getCertificateRequests() {
//        return new ResponseEntity<>(
//                certificateReqSvc.findByStatus(CertificateRequestStatus.PENDING),
//                HttpStatus.OK
//        );
//    }
//
//    @PutMapping("/approve/{certRequestId}/{issuerId}")
//    @PreAuthorize("hasRole('admin')")
//    public ResponseEntity<CertificateRequestDto> approveCertificateRequest(
//            @PathVariable("certRequestId") Long certRequestId,
//            @PathVariable("issuerId") Long issuerId) {
//
//        return new ResponseEntity<>(
//                certificateReqSvc.approveCertificateSignRequest(certRequestId, issuerId),
//                HttpStatus.OK
//        );
//
//    }
//
//    @PutMapping("/decline/{id}")
//    @PreAuthorize("hasRole('admin')")
//    public ResponseEntity<CertificateRequestDto> declineCertificateRequest(@PathVariable("id") Long id) {
//
//        return new ResponseEntity<>(
//                certificateReqSvc.declineCertificateSignRequest(id),
//                HttpStatus.OK
//        );
//
//    }
//
//    @PostMapping
//    // TODO: pre authorize for siem agent role
//    public ResponseEntity<Integer> createCertificateRequest(@RequestBody CertificateSignedRequestDto certificateReq) {
//
//            return new ResponseEntity<>(
//                    certificateReqSvc.createCertificateSignRequest(certificateReq),
//                    HttpStatus.CREATED
//            );
//
//    }
//
//    @GetMapping("/download/{id}")
//    public ResponseEntity<InputStreamResource> downloadRequestedCertificate(@PathVariable Long id) {
//        HttpHeaders headers = new HttpHeaders();
//        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
//        headers.add("Pragma", "no-cache");
//        headers.add("Expires", "0");
//        headers.setContentDispositionFormData("attachment", "ceritifacte.cer");
//
//        return new ResponseEntity<>(
//                certificateReqSvc.getCertFileForCertRequest(id),
//                headers,
//                HttpStatus.OK);
//    }
}
