package bsep.sc.SiemCenter.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class LogSearchDTO {
    @NotNull @Positive private Integer pageNum;
    @NotNull @Positive private Integer pageSize;

    @JsonFormat(pattern = "dd.MM.yyyy HH:mm:ss") private Date lowerGenericTimestamp;
    @JsonFormat(pattern = "dd.MM.yyyy HH:mm:ss") private Date upperGenericTimestamp;
    @JsonFormat(pattern = "dd.MM.yyyy HH:mm:ss") private Date lowerRecievedAt;
    @JsonFormat(pattern = "dd.MM.yyyy HH:mm:ss") private Date upperRecievedAt;

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
