package bsep.sc.SiemCenter.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.kie.api.definition.rule.All;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Document
@Getter
@Setter
public class Rule {

    @Id
    private UUID id;

    private String ruleName; // must be unique

    private String ruleContent;

    public Rule() {
        id = UUID.randomUUID();
    }

    public Rule(String ruleContent, String ruleName) {
        id = UUID.randomUUID();
        this.ruleContent = ruleContent;
        this.ruleName = ruleName;
    }
}
