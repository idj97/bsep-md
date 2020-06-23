package bsep.sc.SiemCenter.controller;

import bsep.sc.SiemCenter.model.Log;
import bsep.sc.SiemCenter.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/agents/api/logs")
public class AgentLogController {

    @Autowired
    private LogService logService;

    @PostMapping
    public ResponseEntity addLogs(@RequestBody List<Log> logs) {
        logService.addLogs(logs);
        return new ResponseEntity(HttpStatus.OK);
    }
}
