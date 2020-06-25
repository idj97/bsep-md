package bsep.pki.PublicKeyInfrastructure.controller;

import bsep.pki.PublicKeyInfrastructure.dto.*;
import bsep.pki.PublicKeyInfrastructure.service.CertificateService;
import bsep.pki.PublicKeyInfrastructure.utility.X500Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/certificates")
public class CertificateController {

    @Autowired
    private CertificateService certificateService;

    @Autowired
    private X500Service x500Service;

    @GetMapping("/serial-number")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<Integer> getSerialNumber() {
        return new ResponseEntity<>(x500Service.generateSerialNumber(), HttpStatus.OK);
    }

    @GetMapping("/authorities")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<List<CertificateDto>> getCaCertificates() {
        return new ResponseEntity<>(certificateService.getCaCertificates(), HttpStatus.OK);
    }

    @PostMapping("/simple-search")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<PageDto<CertificateDto>> simpleSearch(@RequestBody @Valid CertificateSearchDto certificateSearchDto) {
        return new ResponseEntity<>(certificateService.search(certificateSearchDto), HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<CertificateDto> createCertificate(@RequestBody @Valid CreateCertificateDto createCertificateDto) {
        return new ResponseEntity<>(certificateService.create(createCertificateDto), HttpStatus.OK);
    }

    //TODO: correct REST revoke endpoints
    //TODO: Post revocation
    //TODO: Delete revocation
    @PostMapping("/revoke")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<CertificateDto> revoke(@RequestBody @Valid RevocationDto revocationDto) {
        return new ResponseEntity<>(certificateService.revoke(revocationDto), HttpStatus.OK);
    }

    @GetMapping("/download/{serial-number}")
    public ResponseEntity<InputStreamResource> downloadRequestedCertificate(@PathVariable("serial-number") String serialNumber) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");
        headers.setContentDispositionFormData("attachment", "certificate.cer");

        return new ResponseEntity<>(
                certificateService.getCertFileBySerialNumber(serialNumber),
                headers,
                HttpStatus.OK);
    }

    @GetMapping("/pemHead/{serial-number}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<InputStreamResource> downloadPemHeadCertificate(@PathVariable("serial-number") String serialNumber) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");
        headers.setContentDispositionFormData("attachment", "certificate.cer");

        return new ResponseEntity<>(
                certificateService.getPemHeadFileBySerialNumber(serialNumber),
                headers,
                HttpStatus.OK);
    }

    @GetMapping("/pemChain/{serial-number}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<InputStreamResource> downloadPemChainCertificate(@PathVariable("serial-number") String serialNumber) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");
        headers.setContentDispositionFormData("attachment", "certificate.cer");

        return new ResponseEntity<>(
                certificateService.getPemChainFileBySerialNumber(serialNumber),
                headers,
                HttpStatus.OK);
    }

    @GetMapping("/pkcs12/{serial-number}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<InputStreamResource> downloadPKCS12(@PathVariable("serial-number") String serialNumber) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");
        headers.setContentDispositionFormData("attachment", "certificate.p12");

        return new ResponseEntity<>(
                certificateService.getCertPKCS12BySerialNumber(serialNumber),
                headers,
                HttpStatus.OK);
    }
}
