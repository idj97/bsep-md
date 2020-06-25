package bsep.sc.SiemCenter.model;

import lombok.Getter;
import lombok.Setter;
import org.kie.api.definition.type.Role;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.UUID;

@Role(Role.Type.EVENT)
@Document
@Getter
@Setter
public class Log {
    @Id
    private UUID id;

    private String timestamp;
    private String genericTimestamp;
    private Date genericTimestampDate;
    private Date dateReceived;

    private String machineIp;
    private String machineOS;
    private String machineName;
    private String agentInfo;

    private String eventId;
    private String eventName;
    private String eventType;
    private String message;
    private String logSource;
    private String rawText;

    private String source;
    private String sourceIp;
    private String sourcePort;
    private String protocol;
    private Integer duration = 0;
    private Integer size = 0;
    private Double serverThreadsUsage = 0.0;
    private Double serverCoresUsage = 0.0;

    private String action;
    private String command;
    private String workingDir;
    private String sourceUser;
    private String targetUser;

    private String signingAlgorithm;
    private String signature;

    public Log() {
        this.id = UUID.randomUUID();
        this.dateReceived = new Date();
    }
}
