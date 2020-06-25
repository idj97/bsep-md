package bsep.sc.SiemCenter.controller;

import bsep.sc.SiemCenter.dto.PageDTO;
import bsep.sc.SiemCenter.dto.logs.LogDTO;
import bsep.sc.SiemCenter.dto.logs.LogSearchDTO;
import bsep.sc.SiemCenter.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/logs")
public class LogController {

    @Autowired
    private LogService logService;

    @PostMapping("/search")
    public ResponseEntity<PageDTO<LogDTO>> searchLogs(@RequestBody @Valid LogSearchDTO logSearchDTO) {
        return new ResponseEntity<>(logService.searchLogs(logSearchDTO), HttpStatus.OK);
    }
}
