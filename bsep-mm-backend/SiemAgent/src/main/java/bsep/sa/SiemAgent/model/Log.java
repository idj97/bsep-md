package bsep.sa.SiemAgent.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Log {
    private String timestamp = "";
    private Integer facility = 0;
    private Integer severity = 0;
    private String machineIp = "";
    private String machineOS = "";
    private String agentInfo = "";
    private String eventType = "";
    private String message = "";

    private String sourceIp = "";
    private String sourcePort = "";
    private String destIp = "";
    private String destPort = "";
    private String protocol = "";

    private String action = "";
    private String command = "";
    private String workingDir = "";
    private String status = "";
    private String sourceUser = "";
    private String targetUser = "";
}
