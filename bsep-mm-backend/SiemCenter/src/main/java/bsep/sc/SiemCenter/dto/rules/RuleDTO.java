package bsep.sc.SiemCenter.dto.rules;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RuleDTO {
    private UUID id;
    private String ruleName;
    private String ruleContent;

    public RuleDTO(String ruleName, String ruleContent) {
        this.ruleName = ruleName;
        this.ruleContent = ruleContent;
    }
}
