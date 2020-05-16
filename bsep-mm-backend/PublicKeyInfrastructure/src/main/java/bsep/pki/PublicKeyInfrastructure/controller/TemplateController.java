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
    public ResponseEntity<List<TemplateDto>> create(@RequestBody @Valid TemplateDto templateDto) {
        return new ResponseEntity<>(templateService.create(templateDto), HttpStatus.OK);
    }

    @GetMapping
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<List<TemplateDto>> getAll() {
        return new ResponseEntity<>(templateService.getAll(), HttpStatus.OK);
    }

    @DeleteMapping("/{name}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<HttpStatus> delete(@PathVariable String name) {
        templateService.delete(name);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
