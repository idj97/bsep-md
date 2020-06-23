package bsep.sc.SiemCenter.service;

import bsep.sc.SiemCenter.dto.LogSearchDTO;
import bsep.sc.SiemCenter.dto.PageDTO;
import bsep.sc.SiemCenter.model.Log;
import bsep.sc.SiemCenter.repository.LogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class LogService {

    @Autowired
    private LogRepository logRepository;

    @Autowired
    private DateService dateService;

    public void addLogs(List<Log> logs) {
        logs.forEach(log -> log.setGenericTimestampDate(new Date(log.getGenericTimestamp())));
        logRepository.saveAll(logs);
    }

    public PageDTO<Log> searchLogs(LogSearchDTO logSearchDTO) {
        Pageable pageable = PageRequest.of(logSearchDTO.getPageNum(), logSearchDTO.getPageSize());
        logSearchDTO = validateDates(logSearchDTO);
        Page<Log> logPage = logRepository.search(
                logSearchDTO.getTimestamp(),
                logSearchDTO.getLowerGenericTimestamp(),
                logSearchDTO.getUpperGenericTimestamp(),
                logSearchDTO.getLowerRecievedAt(),
                logSearchDTO.getUpperRecievedAt(),
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

        PageDTO<Log> logPageDTO = new PageDTO<Log>(
                logPage.getTotalPages(),
                logPage.getTotalElements(),
                logPage.getContent());

        return logPageDTO;
    }

    public LogSearchDTO validateDates(LogSearchDTO logSearchDTO) {
        System.out.println(dateService.getMinDate());
        System.out.println(dateService.getMaxDate());

        System.out.println(logSearchDTO.getLowerGenericTimestamp());
        if (logSearchDTO.getLowerGenericTimestamp() == null) {
            logSearchDTO.setLowerGenericTimestamp(dateService.getMinDate());
        }
        System.out.println(logSearchDTO.getLowerGenericTimestamp());

        System.out.println(logSearchDTO.getUpperGenericTimestamp());
        if (logSearchDTO.getUpperGenericTimestamp() == null) {
            logSearchDTO.setUpperGenericTimestamp(dateService.getMaxDate());
        }
        System.out.println(logSearchDTO.getUpperGenericTimestamp());


        System.out.println(logSearchDTO.getLowerRecievedAt());
        if (logSearchDTO.getLowerRecievedAt() == null) {
            logSearchDTO.setLowerRecievedAt(dateService.getMinDate());
        }
        System.out.println(logSearchDTO.getLowerRecievedAt());

        System.out.println(logSearchDTO.getUpperRecievedAt());
        if (logSearchDTO.getUpperRecievedAt() == null) {
            logSearchDTO.setUpperRecievedAt(dateService.getMaxDate());
        }
        System.out.println(logSearchDTO.getUpperRecievedAt());

        return logSearchDTO;
    }
}
