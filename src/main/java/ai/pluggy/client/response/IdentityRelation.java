package ai.pluggy.client.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdentityRelation {

  // 'Mother' | 'Father' | 'Spouse'
  String type;
  String name;
  String document;
}
