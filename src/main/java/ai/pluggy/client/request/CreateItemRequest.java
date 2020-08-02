package ai.pluggy.client.request;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class CreateItemRequest {

  Integer connectorId;
  ParametersMap parameters;
  String webhookUrl;

  public CreateItemRequest(Integer connectorId, ParametersMap parameters) {
    this.connectorId = connectorId;
    this.parameters = parameters;
    this.webhookUrl = null;
  }
}
