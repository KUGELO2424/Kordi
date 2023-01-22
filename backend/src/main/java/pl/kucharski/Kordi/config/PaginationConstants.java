package pl.kucharski.Kordi.config;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Locale;

public class PaginationConstants {

    public static final String DEFAULT_PAGE_NUMBER = "0";
    public static final String DEFAULT_PAGE_SIZE = "10";
    public static final String DEFAULT_SORT_BY = "startTime";
    public static final String DEFAULT_SORT_BY_ITEM = "submitTime";
    public static final String DEFAULT_SORT_DIRECTION = "desc";

    public static Pageable getPageable(int pageNo, int pageSize, String sortBy, String sortDirection) {
        Sort.Direction direction;
        try {
            direction = Sort.Direction.valueOf(sortDirection.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    ex.getMessage());
        }
        Sort.Order order = new Sort.Order(direction, sortBy);
        return PageRequest.of(pageNo, pageSize, Sort.by(order));
    }

}
