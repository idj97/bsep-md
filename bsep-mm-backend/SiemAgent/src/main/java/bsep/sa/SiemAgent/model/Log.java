package bsep.sa.SiemAgent.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Log {
    private Date createdAt;
    private Date dateSent;
    private Integer facility;
    private Integer severity;
    private String machineSource;
    private String machineOS;
    private String logFileSource;

    private String sourceIp;
    private String sourcePort;
    private String destIp;
    private String destPort;
    private String protocol;

    private String action;
    private String command;
    private String workingDir;
    private String status;
    private String sourceUser;
    private String targetUser;
    private String eventType;
    private String message;
}
