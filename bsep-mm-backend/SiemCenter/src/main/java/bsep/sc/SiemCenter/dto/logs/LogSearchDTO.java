package bsep.sc.SiemCenter.dto.logs;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.*;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class LogSearchDTO {
    @NotNull @PositiveOrZero private Integer pageNum;
    @NotNull @Positive private Integer pageSize;

    @NotBlank String timezone;
    @JsonFormat(pattern = "dd.MM.yyyy HH:mm:ss") private String lowerGenericTimestamp = "";
    @JsonFormat(pattern = "dd.MM.yyyy HH:mm:ss") private String upperGenericTimestamp = "";
    @JsonFormat(pattern = "dd.MM.yyyy HH:mm:ss") private String lowerRecievedAt = "";
    @JsonFormat(pattern = "dd.MM.yyyy HH:mm:ss") private String upperRecievedAt = "";
    @Null private Date lowerGenericTimestampDate;
    @Null private Date upperGenericTimestampDate;
    @Null private Date lowerRecievedAtDate;
    @Null private Date upperRecievedAtDate;

    private String timestamp = "";
    private String machineIp = "";
    private String machineOS = "";
    private String machineName = "";
    private String agentInfo = "";
    private String eventId = "";
    private String eventName = "";
    private String eventType = "";
    private String message = "";
    private String logSource = "";
    private String rawText = "";
    private String source = "";
    private String sourceIp = "";
    private String sourcePort = "";
    private String protocol = "";
    private String action = "";
    private String command = "";
    private String workingDir = "";
    private String sourceUser = "";
    private String targetUser = "";
}
