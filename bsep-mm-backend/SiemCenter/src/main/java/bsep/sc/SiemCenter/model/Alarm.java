package bsep.sc.SiemCenter.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Document
@Getter
@Setter
public class Alarm {

    @Id
    private UUID id;
    private String name;
    private String description;
    private String machineIp;
    private String machineOS;
    private String machineName;
    private String agentInfo;
    private Date timestamp;
    private String alarmType;
    private List<Log> logs;

    public Alarm() {
        this.id = UUID.randomUUID();
    }

    public Alarm(String name, String description, String alarmType, String machineIp, String machineOs, String machineName, String agentInfo, Date timestamp, List<Log> logs) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.description = description;
        this.machineIp = machineIp;
        this.machineOS = machineOs;
        this.machineName = machineName;
        this.agentInfo = agentInfo;
        this.timestamp = timestamp;
        this.alarmType = alarmType;
        this.logs = logs;
    }
}
