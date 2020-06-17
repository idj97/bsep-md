package bsep.sc.SiemCenter.controller;

import bsep.sc.SiemCenter.dto.RuleTemplate;
import bsep.sc.SiemCenter.events.LogEvent;
import bsep.sc.SiemCenter.service.AlarmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/alarm")
public class AlarmController {

    @Autowired
    private AlarmService alarmService;

    @GetMapping("/create/{templateName}")
    public ResponseEntity<String> createRule(@RequestBody RuleTemplate ruleTemplate, @PathVariable String templateName) {
        return ResponseEntity.ok(alarmService.createRule(ruleTemplate, templateName));
    }

    @GetMapping("/insert")
    public ResponseEntity<Integer> createRule(@RequestBody LogEvent logEvent) {
        return ResponseEntity.ok(alarmService.insertLogEvent(logEvent));
    }

}
