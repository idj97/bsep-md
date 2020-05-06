package bsep.pki.PublicKeyInfrastructure.controller;

import bsep.pki.PublicKeyInfrastructure.dto.TemplateDto;
import bsep.pki.PublicKeyInfrastructure.service.TemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/templates")
public class TemplateController {

    @Autowired
    private TemplateService templateService;

    @PostMapping
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<HttpStatus> createTemplate(@RequestBody @Valid TemplateDto templateDto) {
        templateService.saveTemplate(templateDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<List<TemplateDto>> getTemplates() {
        return new ResponseEntity<>(templateService.getTemplates(), HttpStatus.OK);
    }

}
