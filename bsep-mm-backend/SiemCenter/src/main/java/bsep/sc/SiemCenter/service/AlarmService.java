package bsep.sc.SiemCenter.service;

import bsep.sc.SiemCenter.dto.PageDTO;
import bsep.sc.SiemCenter.dto.alarms.AlarmDTO;
import bsep.sc.SiemCenter.dto.alarms.AlarmSearchDTO;
import bsep.sc.SiemCenter.model.Alarm;
import bsep.sc.SiemCenter.repository.AlarmRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Service
public class AlarmService {

    @Autowired
    private DateService dateService;

    @Autowired
    private AlarmRepository alarmRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    public PageDTO<AlarmDTO> search(AlarmSearchDTO alarmSearchDTO) {
        Pageable pageable = PageRequest.of(alarmSearchDTO.getPageNum(), alarmSearchDTO.getPageSize(), Sort.by("timestamp").descending());
        alarmSearchDTO = setSearchDates(alarmSearchDTO);

        Page<Alarm> alarmsPage = alarmRepository.search(
                alarmSearchDTO.getLowerTimestampDate(),
                alarmSearchDTO.getUpperTimestampDate(),
                alarmSearchDTO.getName(),
                alarmSearchDTO.getDescription(),
                alarmSearchDTO.getMachineIp(),
                alarmSearchDTO.getMachineOS(),
                alarmSearchDTO.getMachineName(),
                alarmSearchDTO.getAgentInfo(),
                alarmSearchDTO.getAlarmType(),
                pageable
        );

        String timezone = alarmSearchDTO.getTimezone();
        List<AlarmDTO> alarmDTOS = alarmsPage.getContent().stream()
                .map(alarm -> new AlarmDTO(alarm, timezone))
                .collect(Collectors.toList());

        PageDTO<AlarmDTO> alarmDTOPageDTO = new PageDTO<>(
                alarmsPage.getTotalPages(),
                alarmsPage.getTotalElements(),
                alarmDTOS);

        return alarmDTOPageDTO;
    }

    public AlarmSearchDTO setSearchDates(AlarmSearchDTO alarmSearchDTO) {
        String lowerTimestamp = alarmSearchDTO.getLowerTimestamp();
        String upperTimestamp = alarmSearchDTO.getUpperTimestamp();
        String timezone = alarmSearchDTO.getTimezone();

        if (lowerTimestamp.equals("")) {
            alarmSearchDTO.setLowerTimestampDate(dateService.getMinDate());
        } else {
            alarmSearchDTO.setLowerTimestampDate(dateService.getDate(lowerTimestamp, timezone));
        }

        if (upperTimestamp.equals("")) {
            alarmSearchDTO.setUpperTimestampDate(dateService.getMaxDate());
        } else {
            alarmSearchDTO.setUpperTimestampDate(dateService.getDate(upperTimestamp, timezone));
        }

        return alarmSearchDTO;
    }

    public void add(Alarm alarm) {
        List<Alarm> alarms = mongoTemplate.find(
                query(where("name").is(alarm.getName())
                        .and("description").is(alarm.getDescription())
                        .and("machineIp").is(alarm.getMachineIp())
                        .and("machineName").is(alarm.getMachineName())
                        .and("machineOS").is(alarm.getMachineOS())
                        .and("alarmType").is(alarm.getAlarmType())
                        .and("agentInfo").is(alarm.getAgentInfo())
                        .and("logs").all(alarm.getLogs())
                ), Alarm.class);

        if (alarms.isEmpty()) {
            alarmRepository.save(alarm);
        }
    }
}
