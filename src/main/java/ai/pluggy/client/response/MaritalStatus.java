package ai.pluggy.client.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MaritalStatus {

  MaritalStatusCode code;
  /** Free-text complement. Populated when code is OTHER. */
  String additionalInfo;
}
