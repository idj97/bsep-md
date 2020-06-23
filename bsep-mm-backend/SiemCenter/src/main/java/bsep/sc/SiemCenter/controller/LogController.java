package bsep.sc.SiemCenter.controller;

import bsep.sc.SiemCenter.dto.LogSearchDTO;
import bsep.sc.SiemCenter.dto.PageDTO;
import bsep.sc.SiemCenter.model.Log;
import bsep.sc.SiemCenter.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/unsecuredd")
public class LogController {

    @Autowired
    private LogService logService;

    @PostMapping("/search")
    public ResponseEntity<PageDTO<Log>> searchLogs(@RequestBody LogSearchDTO logSearchDTO) {
        return new ResponseEntity<>(logService.searchLogs(logSearchDTO), HttpStatus.OK);
    }
}
