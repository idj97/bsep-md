package bsep.sc.SiemCenter.controller;

import bsep.sc.SiemCenter.dto.RuleTemplate;
import bsep.sc.SiemCenter.events.LogEvent;
import bsep.sc.SiemCenter.service.RuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rule")
public class RuleController {

    @Autowired
    private RuleService ruleService;

    // TODO: figure out why post is forbidden

    @GetMapping("/create/{templateName}")
    public ResponseEntity<String> createRule(@RequestBody RuleTemplate ruleTemplate, @PathVariable String templateName) {
        return ResponseEntity.ok(ruleService.createRule(ruleTemplate, templateName));
    }

    @GetMapping("/insert") // TODO: PutMApping
    public ResponseEntity<Integer> insertLogEvent(@RequestBody LogEvent logEvent) {
        return ResponseEntity.ok(ruleService.insertLogEvent(logEvent));
    }

}
