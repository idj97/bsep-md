package bsep.pki.PublicKeyInfrastructure.utility;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PageService {

    public <T> Page<T> getPage(List<T> items, Pageable pageable) {
        long start = pageable.getOffset();
        long end = (start + pageable.getPageSize()) > items.size() ? items.size() : (start + pageable.getPageSize());
        return new PageImpl<>(items.subList((int)start, (int)end), pageable, items.size());
    }
}
