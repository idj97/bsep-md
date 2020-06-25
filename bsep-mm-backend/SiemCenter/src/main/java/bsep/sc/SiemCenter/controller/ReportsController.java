package bsep.sc.SiemCenter.controller;

import bsep.sc.SiemCenter.dto.alarms.AlarmDTO;
import bsep.sc.SiemCenter.dto.alarms.MonthlyAlarmReportDTO;
import bsep.sc.SiemCenter.dto.logs.LogDTO;
import bsep.sc.SiemCenter.dto.logs.MonthlyLogReportDTO;
import bsep.sc.SiemCenter.service.AlarmService;
import bsep.sc.SiemCenter.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportsController {

    @Autowired
    public LogService logService;

    @Autowired
    public AlarmService alarmService;

    @GetMapping("/logs/total")
    public ResponseEntity<Integer> getTotalLogs() {
        return new ResponseEntity<>(logService.getTotalLogs(), HttpStatus.OK);
    }

    @GetMapping("/logs/last-month")
    public ResponseEntity<List<LogDTO>> getLogsFromLastMonth() {
        return new ResponseEntity<>(logService.getLogsFromLastMonth(), HttpStatus.OK);
    }

    @GetMapping("/logs/last-month-by-day")
    public ResponseEntity<List<MonthlyLogReportDTO>> getMonthlyLogReport() {
        return new ResponseEntity<>(logService.getMonthlyLogReport(), HttpStatus.OK);
    }

    @GetMapping("/alarms/total")
    public ResponseEntity<Integer> getTotalAlarms() {
        return new ResponseEntity<>(alarmService.getTotalAlarms(), HttpStatus.OK);
    }

    @GetMapping("/alarms/last-month")
    public ResponseEntity<List<AlarmDTO>> getAlarmsFromLastMonth() {
        return new ResponseEntity<>(alarmService.getAlarmsFromLastMonth(), HttpStatus.OK);
    }

    @GetMapping("/alarms/last-month-by-day")
    public ResponseEntity<List<MonthlyAlarmReportDTO>> getMonthlyAlarmsReport() {
        return new ResponseEntity<>(alarmService.getMonthlyAlarmsReport(), HttpStatus.OK);
    }
}
