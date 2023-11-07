package ai.pluggy.client.response;

import lombok.Data;

@Data
public class Category {

  String id;
  String description;
  String parentId;
  String parentDescription;
}
