package bsep.sc.SiemCenter.controller;

import bsep.sc.SiemCenter.dto.RuleDto;
import bsep.sc.SiemCenter.dto.RuleTemplate;
import bsep.sc.SiemCenter.events.LogEvent;
import bsep.sc.SiemCenter.service.RuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<List<RuleDto>> getAllRules() {
        return ResponseEntity.ok(ruleService.getAllRules());
    }


    @GetMapping("/simple-template")
    public ResponseEntity<String> getSimpleTemplate() {
        return ResponseEntity.ok(ruleService.getTemplate(simpleTemplatePath));
    }


    @GetMapping("/cep-template")
    public ResponseEntity<String> getCepTemplate()
    {
        return ResponseEntity.ok(ruleService.getTemplate(cepTemplatePath));
    }


    @PostMapping
    public ResponseEntity<Boolean> createRule(@RequestBody RuleDto ruleDto) {
        return ResponseEntity.ok(ruleService.createRule(ruleDto));
    }

    /*
    @PostMapping("/{templateName}")
    public ResponseEntity<String> createRule(@RequestBody RuleTemplate ruleTemplate, @PathVariable String templateName) {
        return ResponseEntity.ok(ruleService.createRule(ruleTemplate, templateName));
    }*/

    @PutMapping("/insert") // TODO: PutMApping
    public ResponseEntity<Integer> insertLogEvent(@RequestBody LogEvent logEvent) {
        return ResponseEntity.ok(ruleService.insertLogEvent(logEvent));
    }

}
