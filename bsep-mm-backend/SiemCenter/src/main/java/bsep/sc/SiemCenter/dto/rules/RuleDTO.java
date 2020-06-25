package bsep.sc.SiemCenter.dto.rules;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RuleDTO {

    private UUID id;

    private String ruleName;

    @NotBlank(message = "Rule can not be empty")
    private String ruleContent;

    public RuleDTO(String ruleName, String ruleContent) {
        this.ruleName = ruleName;
        this.ruleContent = ruleContent;
    }
}
