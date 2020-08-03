package ai.pluggy.client.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class UpdateItemRequest {
  Integer connectorId;
  ParametersMap parameters;
  String webhookUrl;
}
