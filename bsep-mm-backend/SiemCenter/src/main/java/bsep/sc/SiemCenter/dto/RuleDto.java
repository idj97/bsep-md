package bsep.sc.SiemCenter.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RuleDto {

    private UUID id;
    private String ruleName;
    private String ruleContent;
}
