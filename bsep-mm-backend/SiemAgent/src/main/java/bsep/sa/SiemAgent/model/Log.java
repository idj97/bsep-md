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
    private String logSource = "";
    private String logType = "";
    private String rawText = "";

    private String source = "";
    private String sourceIp = "";
    private String sourcePort = "";
    private String protocol = "";
    private String duration = "0";
    private String size = "0";
    private String serverThreadsUsage = "0.0";
    private String serverCoresUsage = "0.0";

    private String action = "";
    private String command = "";
    private String workingDir = "";
    private String sourceUser = "";
    private String targetUser = "";

    private String signingAlgorithm;
    private String signature;
}
