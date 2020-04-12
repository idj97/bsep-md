package bsep.pki.PublicKeyInfrastructure.controller;

import bsep.pki.PublicKeyInfrastructure.dto.CADto;
import bsep.pki.PublicKeyInfrastructure.model.CAType;
import bsep.pki.PublicKeyInfrastructure.service.CAService;
import bsep.pki.PublicKeyInfrastructure.service.RootCAService;
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

    @Autowired
    private RootCAService rootCAService;

    @PostMapping
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<CADto> create(@RequestBody @Valid CADto caDto) {
        CADto caDtoRet = null;
        if (caDto.getCaType().equals(CAType.ROOT)) {
            caDtoRet = rootCAService.createRootCA(caDto);
        } else {
            caDtoRet = caService.createCA(caDto);
        }
        return new ResponseEntity<>(caDtoRet, HttpStatus.OK);
    }
}