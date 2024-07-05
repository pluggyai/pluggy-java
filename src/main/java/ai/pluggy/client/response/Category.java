package ai.pluggy.client.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Category {

  String id;
  String description;
  String parentId;
  String parentDescription;
}
