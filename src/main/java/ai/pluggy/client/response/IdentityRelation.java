package ai.pluggy.client.response;

import lombok.Data;

@Data
public class IdentityRelation {

  // 'Mother' | 'Father' | 'Spouse'
  String type;
  String name;
  String document;
}
