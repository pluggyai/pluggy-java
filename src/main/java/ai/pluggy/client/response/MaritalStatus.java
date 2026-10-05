package ai.pluggy.client.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaritalStatus {

  MaritalStatusCode code;
  /** Free-text complement. Populated when code is OTHER. */
  String additionalInfo;
}
