package bsep.pki.PublicKeyInfrastructure.controller;

import bsep.pki.PublicKeyInfrastructure.dto.*;
import bsep.pki.PublicKeyInfrastructure.service.CRLService;
import bsep.pki.PublicKeyInfrastructure.service.CertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/certificates")
public class CertificateController {

    @Autowired
    private CRLService crlService;

    @Autowired
    private CertificateService certificateService;

    @PostMapping("/revoke")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<CertificateDto> revoke(@RequestBody @Valid RevocationDto revocationDto) {
        return new ResponseEntity<>(crlService.revokeCertificate(revocationDto), HttpStatus.OK);
    }

    @PostMapping("/simple-search")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<PageDto<CertificateDto>> simpleSearch(@RequestBody @Valid CertificateSearchDto certificateSearchDto) {
        return new ResponseEntity<>(certificateService.search(certificateSearchDto), HttpStatus.OK);
    }

    @GetMapping("/download/{serialNumber}")
    public ResponseEntity<InputStreamResource> downloadRequestedCertificate(@PathVariable String serialNumber) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");
        headers.setContentDispositionFormData("attachment", "ceritifacte.cer");

        return new ResponseEntity<>(
                certificateService.getCertFileBySerialNumber(serialNumber),
                headers,
                HttpStatus.OK);
    }

}
