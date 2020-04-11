package bsep.pki.PublicKeyInfrastructure.controller;

import bsep.pki.PublicKeyInfrastructure.dto.*;
import bsep.pki.PublicKeyInfrastructure.service.CRLService;
import bsep.pki.PublicKeyInfrastructure.service.CertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @PostMapping("/search")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<PageDto<CADto>> search(@RequestBody @Valid CertificateSearchDto certificateSearchDto) {
        return new ResponseEntity<>(certificateService.getAll(certificateSearchDto), HttpStatus.OK);
    }

    @PostMapping("/simple-search")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<PageDto<CertificateDto>> simpleSearch(@RequestBody @Valid CertificateSearchDto certificateSearchDto) {
        return new ResponseEntity<>(certificateService.search(certificateSearchDto), HttpStatus.OK);
    }

}
