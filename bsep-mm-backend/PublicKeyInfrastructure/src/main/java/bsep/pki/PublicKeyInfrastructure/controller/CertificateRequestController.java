package bsep.pki.PublicKeyInfrastructure.controller;

import bsep.pki.PublicKeyInfrastructure.dto.CertificateRequestDto;
import bsep.pki.PublicKeyInfrastructure.dto.CertificateSignedRequestDto;
import bsep.pki.PublicKeyInfrastructure.model.CertificateRequest;
import bsep.pki.PublicKeyInfrastructure.model.CertificateRequestStatus;
import bsep.pki.PublicKeyInfrastructure.service.CertificateRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/certificate-request")
public class CertificateRequestController {

    @Autowired
    CertificateRequestService certificateReqSvc;

    @GetMapping
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<List<CertificateRequestDto>> getCertificateRequests() {
        return new ResponseEntity<>(
                certificateReqSvc.findByStatus(CertificateRequestStatus.PENDING),
                HttpStatus.OK
        );
    }

    @PutMapping("/approve/{id}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<CertificateRequestDto> approveCertificateRequest(@PathVariable("id") Long id) {

        return new ResponseEntity<>(
                certificateReqSvc.approveCertificateSignRequest(id),
                HttpStatus.OK
        );

    }

    @PutMapping("/decline/{id}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<CertificateRequestDto> declineCertificateRequest(@PathVariable("id") Long id) {

        return new ResponseEntity<>(
                certificateReqSvc.declineCertificateSignRequest(id),
                HttpStatus.OK
        );

    }

    @PostMapping
    // TODO: pre authorize for siem agent role
    public ResponseEntity<Integer> createCertificateRequest(@RequestBody CertificateSignedRequestDto certificateReq) {

            return new ResponseEntity<>(
                    certificateReqSvc.createCertificateSignRequest(certificateReq),
                    HttpStatus.CREATED
            );

    }




}
