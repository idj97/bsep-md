package bsep.pki.PublicKeyInfrastructure.controller;

import bsep.pki.PublicKeyInfrastructure.dto.CertificateDto;
import bsep.pki.PublicKeyInfrastructure.service.OcspService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/ocsp")
public class OcspController {

    @Autowired
    private OcspService ocspService;

    @PostMapping(
            consumes = "application/ocsp-request",
            produces = "application/ocsp-response")
    public ResponseEntity<byte[]> processOcspRequest(@RequestBody byte[] request) {
        System.out.println("OCSP CHECK at " + new Date());
        return new ResponseEntity<>(ocspService.getResponse(request), HttpStatus.OK);
    }

    @PostMapping("/signer/{serialNumber}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<CertificateDto> setOcspResponder(@PathVariable String serialNumber) {
        return new ResponseEntity<>(ocspService.setOcspSigner(serialNumber), HttpStatus.OK);
    }
}
