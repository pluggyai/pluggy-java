package ai.pluggy.client.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class IdentityRelation {

  // 'Mother' | 'Father' | 'Spouse'
  String type;
  String name;
  String document;
}
