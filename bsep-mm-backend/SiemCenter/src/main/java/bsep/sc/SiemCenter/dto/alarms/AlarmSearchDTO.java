package bsep.sc.SiemCenter.dto.alarms;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.*;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AlarmSearchDTO {
    @NotNull @PositiveOrZero private Integer pageNum;
    @NotNull @Positive private Integer pageSize;
    @NotBlank String timezone;

    @JsonFormat(pattern = "dd.MM.yyyy HH:mm:ss") private String lowerTimestamp = "";
    @JsonFormat(pattern = "dd.MM.yyyy HH:mm:ss") private String upperTimestamp = "";
    @Null private Date lowerTimestampDate;
    @Null private Date upperTimestampDate;
    private String name = "";
    private String description = "";
    private String machineIp = "";
    private String machineOS = "";
    private String machineName = "";
    private String agentInfo = "";
    private String alarmType = "";
}
