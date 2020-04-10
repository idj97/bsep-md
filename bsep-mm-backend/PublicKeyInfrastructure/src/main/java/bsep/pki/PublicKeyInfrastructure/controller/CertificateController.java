package bsep.pki.PublicKeyInfrastructure.controller;

import bsep.pki.PublicKeyInfrastructure.dto.CertificateDto;
import bsep.pki.PublicKeyInfrastructure.dto.RevocationDto;
import bsep.pki.PublicKeyInfrastructure.service.CRLService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
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

    @PostMapping("/revoke")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<CertificateDto> revoke(@RequestBody @Valid RevocationDto revocationDto) {
        return new ResponseEntity<>(crlService.revokeCertificate(revocationDto), HttpStatus.OK);
    }


}
