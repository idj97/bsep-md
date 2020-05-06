package bsep.pki.PublicKeyInfrastructure.controller;

import bsep.pki.PublicKeyInfrastructure.config.AlgorithmsConfig;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/algorithms")
public class AlgorithmsController {

    @GetMapping("/signing")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<List<String>> getSigningAlgorithms() {
        return new ResponseEntity<>(AlgorithmsConfig.secureSigningAlgorithms, HttpStatus.OK);
    }

    @GetMapping("/key-generation")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<List<String>> getKeyGenerationAlgorithms() {
        return new ResponseEntity<>(AlgorithmsConfig.secureKeyGenerationAlgorithms, HttpStatus.OK);
    }
}
