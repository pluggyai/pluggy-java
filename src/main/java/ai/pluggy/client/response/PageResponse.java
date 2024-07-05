package ai.pluggy.client.response;

import java.util.List;
import lombok.Data;

@Data
public class PageResponse<T> {
    Integer total;
    Integer totalPages;
    Integer page;
    List<T> results;
}
