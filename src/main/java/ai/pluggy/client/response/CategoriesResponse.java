package ai.pluggy.client.response;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoriesResponse {

  List<Category> results;
}
