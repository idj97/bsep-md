package bsep.sc.SiemCenter.dto.logs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyLogReportDTO {
    private int day;
    private int numOfLogs;
}
