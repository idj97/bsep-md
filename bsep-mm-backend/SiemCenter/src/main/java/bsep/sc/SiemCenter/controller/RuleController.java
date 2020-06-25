package bsep.sc.SiemCenter.controller;

import bsep.sc.SiemCenter.dto.rules.RuleDTO;
import bsep.sc.SiemCenter.service.RuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/rule")
public class RuleController {

    @Autowired
    private RuleService ruleService;

    @Value("${drools.templates.simple.path}")
    private String simpleTemplatePath;

    @Value("${drools.templates.cep.path}")
    private String cepTemplatePath;

    @GetMapping
    public ResponseEntity<List<RuleDTO>> getAllRules() {
        return ResponseEntity.ok(ruleService.getAllRules());
    }

    @PostMapping
    public ResponseEntity create(@Valid @RequestBody RuleDTO ruleDTO) {
        ruleService.create(ruleDTO);
        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping("/{ruleName}")
    public ResponseEntity remove(@PathVariable String ruleName) {
        ruleService.remove(ruleName);
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/simple-template")
    public ResponseEntity<String> getSimpleTemplate() {
        return ResponseEntity.ok(ruleService.getTemplate(simpleTemplatePath));
    }

    @GetMapping("/cep-template")
    public ResponseEntity<String> getCepTemplate() {
        return ResponseEntity.ok(ruleService.getTemplate(cepTemplatePath));
    }
}
