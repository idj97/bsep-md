package bsep.sa.SiemAgent.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Log {
    private String timestamp = "";
    private String genericTimestamp = "";

    private String machineIp = "";
    private String machineOS = "";
    private String machineName = "";
    private String agentInfo = "";

    private String eventId = "";
    private String eventName = "";
    private String eventType = "";
    private String message = "";
    private String logFilePath = "";
    private String rawText = "";

    private String source = "";
    private String sourceIp = "";
    private String sourcePort = "";
    private String protocol = "";
    private String duration;
    private String size;

    private String action = "";
    private String command = "";
    private String workingDir = "";
    private String sourceUser = "";
    private String targetUser = "";

}
