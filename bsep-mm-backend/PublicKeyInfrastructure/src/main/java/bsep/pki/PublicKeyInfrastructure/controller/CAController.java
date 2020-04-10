package bsep.pki.PublicKeyInfrastructure.controller;

import bsep.pki.PublicKeyInfrastructure.dto.CADto;
import bsep.pki.PublicKeyInfrastructure.dto.CertificateSearchDto;
import bsep.pki.PublicKeyInfrastructure.dto.PageDto;
import bsep.pki.PublicKeyInfrastructure.service.CAService;
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
@RequestMapping("/api/ca")
public class CAController {

    @Autowired
    private CAService caService;

    @PostMapping
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<CADto> create(@RequestBody @Valid CADto caDto) {
        return new ResponseEntity<>(caService.createCA(caDto), HttpStatus.OK);
    }

    @PostMapping(path = "/search")
    public ResponseEntity<PageDto<CADto>> searchCAs(@RequestBody @Valid CertificateSearchDto certificateSearchDto) {
        return new ResponseEntity<>(caService.getAll(certificateSearchDto), HttpStatus.OK);
    }
}