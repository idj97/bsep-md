package bsep.sc.SiemCenter.dto.alarms;

import bsep.sc.SiemCenter.dto.logs.LogDTO;
import bsep.sc.SiemCenter.model.Alarm;
import bsep.sc.SiemCenter.service.DateService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.kie.api.definition.rule.All;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AlarmDTO {
    private String name;
    private String description;
    private String machineIp;
    private String machineOS;
    private String machineName;
    private String agentInfo;
    private String timestamp;
    private String alarmType;
    private List<LogDTO> logs;

    public AlarmDTO(Alarm alarm, String timezone) {
        name = alarm.getName();
        description = alarm.getDescription();
        machineIp = alarm.getMachineIp();
        machineName = alarm.getMachineName();
        machineOS = alarm.getMachineOS();
        alarmType = alarm.getAlarmType();
        agentInfo = alarm.getAgentInfo();
        machineOS = alarm.getMachineOS();
        timestamp = new DateService().getString(alarm.getTimestamp(), timezone);
        logs = alarm.getLogs().stream().map(log -> new LogDTO(log, timezone)).collect(Collectors.toList());
    }
}
