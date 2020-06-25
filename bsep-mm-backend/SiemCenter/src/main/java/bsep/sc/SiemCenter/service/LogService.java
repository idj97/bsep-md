package bsep.sc.SiemCenter.service;

import bsep.sc.SiemCenter.dto.logs.LogDTO;
import bsep.sc.SiemCenter.dto.logs.LogSearchDTO;
import bsep.sc.SiemCenter.dto.PageDTO;
import bsep.sc.SiemCenter.dto.logs.MonthlyLogReportDTO;
import bsep.sc.SiemCenter.model.Log;
import bsep.sc.SiemCenter.repository.LogRepository;
import bsep.sc.SiemCenter.service.drools.KieSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LogService {

    @Autowired
    private LogRepository logRepository;

    @Autowired
    private DateService dateService;
  
    @Autowired
    private KieSessionService kieSessionService;


    public List<LogDTO> getLogsFromLastMonth() {
        Date currentDate = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(Calendar.MONTH, -1);
        Date lastMonth = calendar.getTime();

        List<Log> logs = logRepository.findByDateReceivedBetween(lastMonth, currentDate);
        return logs.stream().map(log -> new LogDTO(log, "CET")).collect(Collectors.toList());
    }

    public List<MonthlyLogReportDTO> getMonthlyLogReport() {
        Date currentDate = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(Calendar.MONTH, -1);
        Date lastMonth = calendar.getTime();

        Calendar start = Calendar.getInstance();
        start.setTime(lastMonth);
        Calendar end = Calendar.getInstance();
        end.setTime(currentDate);
        end.add(Calendar.DATE, 1);

        List<Log> logs = logRepository.findByDateReceivedBetween(lastMonth, currentDate);

        List<MonthlyLogReportDTO> reports = new ArrayList<>();
        for (Date date = start.getTime(); start.before(end); start.add(Calendar.DATE, 1), date = start.getTime()) {
            MonthlyLogReportDTO mlrDTO = new MonthlyLogReportDTO();
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            mlrDTO.setDay(c.get(Calendar.DAY_OF_MONTH));
            for (Log log : logs) {
                Calendar cal1 = Calendar.getInstance();
                Calendar cal2 = Calendar.getInstance();
                cal1.setTime(date);
                cal2.setTime(log.getDateReceived());

                if (cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR) &&
                        cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)) {

                    mlrDTO.setNumOfLogs(mlrDTO.getNumOfLogs() + 1);

                }
            }
            reports.add(mlrDTO);
        }

        return reports;
    }

    public int getTotalLogs() {
        return logRepository.findAll().size();
    }

    public void addLogs(List<Log> logs) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

        logs.forEach(log -> {
            log.setGenericTimestampDate(new Date(Long.parseLong(log.getGenericTimestamp())));
            kieSessionService.insertEvent(log);
        });
        logRepository.saveAll(logs);
    }

    public PageDTO<LogDTO> searchLogs(LogSearchDTO logSearchDTO) {
        Pageable pageable = PageRequest.of(logSearchDTO.getPageNum(), logSearchDTO.getPageSize(), Sort.by("genericTimestampDate").descending());
        logSearchDTO = setSearchDates(logSearchDTO);
        Page<Log> logPage = logRepository.search(
                logSearchDTO.getTimestamp(),
                logSearchDTO.getLowerGenericTimestampDate(),
                logSearchDTO.getUpperGenericTimestampDate(),
                logSearchDTO.getLowerRecievedAtDate(),
                logSearchDTO.getUpperRecievedAtDate(),
                logSearchDTO.getMachineIp(),
                logSearchDTO.getMachineOS(),
                logSearchDTO.getMachineName(),
                logSearchDTO.getAgentInfo(),
                logSearchDTO.getEventId(),
                logSearchDTO.getEventName(),
                logSearchDTO.getEventType(),
                logSearchDTO.getMessage(),
                logSearchDTO.getLogSource(),
                logSearchDTO.getRawText(),
                logSearchDTO.getSource(),
                logSearchDTO.getSourceIp(),
                logSearchDTO.getSourcePort(),
                logSearchDTO.getProtocol(),
                logSearchDTO.getAction(),
                logSearchDTO.getCommand(),
                logSearchDTO.getWorkingDir(),
                logSearchDTO.getSourceUser(),
                logSearchDTO.getTargetUser(),
                pageable);

        String timezone = logSearchDTO.getTimezone();
        List<LogDTO> logDTOS = logPage.getContent().stream()
                .map(log -> new LogDTO(log, timezone))
                .collect(Collectors.toList());

        PageDTO<LogDTO> logPageDTO = new PageDTO<>(
                logPage.getTotalPages(),
                logPage.getTotalElements(),
                logDTOS);

        return logPageDTO;
    }

    public LogSearchDTO setSearchDates(LogSearchDTO logSearchDTO) {
        String lowerGenericTimestamp = logSearchDTO.getLowerGenericTimestamp();
        String upperGenericTimestamp = logSearchDTO.getUpperGenericTimestamp();
        String lowerRecievedAt = logSearchDTO.getLowerRecievedAt();
        String upperRecievedAt = logSearchDTO.getUpperRecievedAt();
        String timezone = logSearchDTO.getTimezone();

        if (lowerGenericTimestamp.equals("")) {
            logSearchDTO.setLowerGenericTimestampDate(dateService.getMinDate());
        } else {
            logSearchDTO.setLowerGenericTimestampDate(dateService.getDate(lowerGenericTimestamp, timezone));
        }

        if (upperGenericTimestamp.equals("")) {
            logSearchDTO.setUpperGenericTimestampDate(dateService.getMaxDate());
        } else {
            logSearchDTO.setUpperGenericTimestampDate(dateService.getDate(upperGenericTimestamp, timezone));
        }

        if (lowerRecievedAt.equals("")) {
            logSearchDTO.setLowerRecievedAtDate(dateService.getMinDate());
        } else {
            logSearchDTO.setLowerRecievedAtDate(dateService.getDate(lowerRecievedAt, timezone));
        }

        if (upperRecievedAt.equals("")) {
            logSearchDTO.setUpperRecievedAtDate(dateService.getMaxDate());
        } else {
            logSearchDTO.setUpperRecievedAtDate(dateService.getDate(upperRecievedAt, timezone));
        }

        return logSearchDTO;
    }


}
