package bsep.sc.SiemCenter.dto.alarms;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyAlarmReportDTO {
    private int day;
    private int numOfAlarms;
}
