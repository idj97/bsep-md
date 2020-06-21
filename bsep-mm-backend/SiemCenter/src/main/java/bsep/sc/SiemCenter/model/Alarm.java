package bsep.sc.SiemCenter.model;

import bsep.sc.SiemCenter.events.AlarmType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.UUID;

@Document
@Getter
@Setter
public class Alarm {

    @Id
    private UUID id;
    private String machineSource;

    private String name;
    private String description;

    private AlarmType alarmType;
    private Date timeStamp;

    public Alarm() {
        this.id = UUID.randomUUID();
    }

    public Alarm(String machineSource, String name, String description, AlarmType alarmType, Date timeStamp) {
        this.id = UUID.randomUUID();
        this.machineSource = machineSource;
        this.name = name;
        this.description = description;
        this.alarmType = alarmType;
        this.timeStamp = timeStamp;
    }
}
