package bsep.sc.SiemCenter.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Document
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TestMessage {

    @Id
    private UUID id;
    private String message;
}
