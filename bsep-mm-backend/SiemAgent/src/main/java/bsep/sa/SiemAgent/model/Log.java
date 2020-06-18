package bsep.sa.SiemAgent.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Log {
    private String timestamp = "";
    private String machineIp = "";
    private String machineOS = "";
    private String agentInfo = "";
    private String eventName = "";
    private String eventType = "";
    private String message = "";

    private String sourceIp = "";
    private String sourcePort = "";
    private String protocol = "";

    private String action = "";
    private String command = "";
    private String workingDir = "";
    private String sourceUser = "";
    private String targetUser = "";
}
