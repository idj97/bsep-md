package bsep.pki.PublicKeyInfrastructure.controller;

import bsep.pki.PublicKeyInfrastructure.dto.CADto;
import bsep.pki.PublicKeyInfrastructure.service.CAService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

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

    @GetMapping
    public ResponseEntity<List<CADto>> getAll() {
        return new ResponseEntity<>(caService.getAll(), HttpStatus.OK);
    }
}