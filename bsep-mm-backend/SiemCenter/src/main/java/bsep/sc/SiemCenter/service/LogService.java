package bsep.sc.SiemCenter.service;

import bsep.sc.SiemCenter.dto.logs.LogDTO;
import bsep.sc.SiemCenter.dto.logs.LogSearchDTO;
import bsep.sc.SiemCenter.dto.PageDTO;
import bsep.sc.SiemCenter.model.Log;
import bsep.sc.SiemCenter.repository.LogRepository;
import bsep.sc.SiemCenter.service.drools.KieSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
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

    public void addLogs(List<Log> logs) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

        logs.forEach(log -> {
            log.setGenericTimestampDate(new Date(Long.parseLong(log.getGenericTimestamp())));
            kieSessionService.insertEvent(log);
        });
        logRepository.saveAll(logs);
    }

    public PageDTO<LogDTO> searchLogs(LogSearchDTO logSearchDTO) {
        Pageable pageable = PageRequest.of(logSearchDTO.getPageNum(), logSearchDTO.getPageSize());
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
