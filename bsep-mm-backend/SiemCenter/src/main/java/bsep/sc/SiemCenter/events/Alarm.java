package bsep.sc.SiemCenter.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Alarm {

    private String machineSource;

    private String name;
    private String description;

    private AlarmType alarmType;
    private Date timeStamp;
}
