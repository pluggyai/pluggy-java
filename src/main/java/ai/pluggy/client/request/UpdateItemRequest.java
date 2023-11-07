package ai.pluggy.client.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateItemRequest {
  Integer connectorId;
  ParametersMap parameters;
  String webhookUrl;
  String clientUserId;
}
