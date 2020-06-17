package bsep.sc.SiemCenter.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.UUID;

@Document
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Log {

    @Id
    private UUID id;

    private Integer facility;
    private Integer severity;

    private String machineSource;
    private String machineOs;

    private String sourceIp;
    private String sourcePort;

    private String eventType; // warn, info, err
    private Date createdAt;
    private Date dateReceived;
}
