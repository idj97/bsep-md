package bsep.pki.PublicKeyInfrastructure.controller;

import bsep.pki.PublicKeyInfrastructure.config.AlgorithmsConfig;
import bsep.pki.PublicKeyInfrastructure.exception.ApiNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/algorithms")
public class AlgorithmsController {

    @GetMapping("/signing/{key-generation-algorithm}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<List<String>> getSigningAlgorithms(@PathVariable("key-generation-algorithm") String keyGenerationAlgorithm) {
        if (keyGenerationAlgorithm.equals("RSA")) {
            return new ResponseEntity<>(AlgorithmsConfig.secureRSASigningAlgorithms, HttpStatus.OK);
        } else if (keyGenerationAlgorithm.equals("DSA")) {
            return new ResponseEntity<>(AlgorithmsConfig.secureDSASigningAlgorithms, HttpStatus.OK);
        } else {
            throw new ApiNotFoundException("Signing algorithms not found.");
        }
    }

    @GetMapping("/key-generation")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<List<String>> getKeyGenerationAlgorithms() {
        return new ResponseEntity<>(AlgorithmsConfig.secureKeyGenerationAlgorithms, HttpStatus.OK);
    }
}
