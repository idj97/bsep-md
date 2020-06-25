package bsep.sc.SiemCenter.events;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.kie.api.definition.type.Role;

import java.util.Date;

@Role(Role.Type.EVENT)
@NoArgsConstructor
@Getter
@Setter
public class LogEvent {
    private String facility; // must be string to compare in rules
    private String severity; // must be string to compare in rules

    private String machineSource;
    private String machineOs;

    private String sourceIp;
    private String sourcePort;

    private String eventType; // warn, info, err
    private Date createdAt;
    private Date dateReceived;
}
