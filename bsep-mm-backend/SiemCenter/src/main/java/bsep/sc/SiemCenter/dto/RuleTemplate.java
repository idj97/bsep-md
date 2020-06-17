package bsep.sc.SiemCenter.dto;

import bsep.sc.SiemCenter.events.AlarmType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RuleTemplate {

    private String ruleName;
    private String attribute;
    private String comparator;

    private String valueToCompare;
    private String alarmName;
    private String alarmDescription;
    private AlarmType alarmType;

    private int numOfEvents; // the number of events to check
    private String timeFrame; // time frame of events 60s or 1m

}
