package bsep.sa.SiemAgent.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.LinkedList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LogFile {
    private String path;
    private Long readFrequency; // milliseconds
    private List<LogPattern> logPatterns = new LinkedList<>();
}
