package bsep.sc.SiemCenter.controller;

import bsep.sc.SiemCenter.dto.PageDTO;
import bsep.sc.SiemCenter.dto.alarms.AlarmDTO;
import bsep.sc.SiemCenter.dto.alarms.AlarmSearchDTO;
import bsep.sc.SiemCenter.service.AlarmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/alarms")
public class AlarmController {

    @Autowired
    private AlarmService alarmService;

    @PostMapping("/search")
    public ResponseEntity<PageDTO<AlarmDTO>> search(@RequestBody @Valid AlarmSearchDTO alarmSearchDTO) {
        return new ResponseEntity<>(alarmService.search(alarmSearchDTO), HttpStatus.OK);
    }
}
