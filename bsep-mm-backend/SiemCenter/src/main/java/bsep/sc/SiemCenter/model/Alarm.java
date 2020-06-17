package bsep.sc.SiemCenter.model;

import bsep.sc.SiemCenter.events.AlarmType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.UUID;

@Document
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Alarm {

    @Id
    private UUID id;
    private String machineSource;

    private String name;
    private String description;

    private AlarmType alarmType;
    private Date timeStamp;
}
