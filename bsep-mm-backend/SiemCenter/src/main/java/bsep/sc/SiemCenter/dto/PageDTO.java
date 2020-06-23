package bsep.sc.SiemCenter.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PageDTO<T> {
    private Integer totalPages;
    private Long totalSize;
    private List<T> items;
}
