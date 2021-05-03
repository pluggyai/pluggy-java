package ai.pluggy.client.request;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class CreateItemRequest {

  Integer connectorId;
  ParametersMap parameters;
  String webhookUrl;
  String clientUserId;

  public CreateItemRequest(Integer connectorId, ParametersMap parameters) {
    this.connectorId = connectorId;
    this.parameters = parameters;
    this.webhookUrl = null;
    this.clientUserId = null;
  }

  public CreateItemRequest(Integer connectorId, ParametersMap parameters, String webhookUrl) {
    this.connectorId = connectorId;
    this.parameters = parameters;
    this.webhookUrl = webhookUrl;
    this.clientUserId = null;
  }
}
