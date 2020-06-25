package bsep.sc.SiemCenter.dto.logs;

import bsep.sc.SiemCenter.model.Log;
import bsep.sc.SiemCenter.service.DateService;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class LogDTO {
    private String timestamp;
    private String genericTimestampDate;
    private String dateReceived;

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
    private String duration;
    private String size;
    private String serverThreadsUsage;
    private String serverCoresUsage;

    private String action;
    private String command;
    private String workingDir;
    private String sourceUser;
    private String targetUser;

    public LogDTO(Log log, String timezone) {
        timestamp = log.getTimestamp();

        machineIp = log.getMachineIp();
        machineOS = log.getMachineOS();
        machineName = log.getMachineName();
        agentInfo = log.getAgentInfo();

        eventId = log.getEventId();
        eventName = log.getEventName();
        eventType = log.getEventType();
        message = log.getMessage();
        logSource = log.getLogSource();
        rawText = log.getRawText();

        source = log.getSource();
        sourceIp = log.getSourceIp();
        sourcePort = log.getSourcePort();
        protocol = log.getProtocol();
        duration = log.getDuration().toString();
        size = log.getSize().toString();
        serverThreadsUsage = log.getServerThreadsUsage().toString();
        serverCoresUsage = log.getServerCoresUsage().toString();

        action = log.getAction();
        command = log.getCommand();
        workingDir = log.getWorkingDir();
        sourceUser = log.getSourceUser();
        targetUser = log.getTargetUser();

        DateService dateService = new DateService();
        if (log.getGenericTimestampDate() != null)
            genericTimestampDate = dateService.getString(log.getGenericTimestampDate(), timezone);
        else
            genericTimestampDate = "";

        if (log.getDateReceived() != null)
            dateReceived = dateService.getString(log.getDateReceived(), timezone);
        else
            dateReceived = "";
    }
}
