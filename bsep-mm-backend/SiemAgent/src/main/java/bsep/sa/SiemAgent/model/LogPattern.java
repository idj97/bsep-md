package bsep.sa.SiemAgent.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LogPattern {
    private String name;
    private Integer severity;
    private Integer facility;
    private String type;
    private String pattern;
}
