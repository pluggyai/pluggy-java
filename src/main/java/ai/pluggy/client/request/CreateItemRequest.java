package ai.pluggy.client.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@Value
@AllArgsConstructor
@Builder
public class CreateItemRequest {
  Integer connectorId;
  ParametersMap parameters;
  String webhookUrl;
  String clientUserId;
  ProductType[] products;

  public CreateItemRequest(Integer connectorId, ParametersMap parameters) {
    this.connectorId = connectorId;
    this.parameters = parameters;
    this.webhookUrl = null;
    this.clientUserId = null;
    this.products = null;
  }

  public CreateItemRequest(Integer connectorId, ParametersMap parameters, String webhookUrl) {
    this.connectorId = connectorId;
    this.parameters = parameters;
    this.webhookUrl = webhookUrl;
    this.clientUserId = null;
    this.products = null;
  }

  public CreateItemRequest(Integer connectorId, ParametersMap parameters, String webhookUrl, String clientUserId) {
    this.connectorId = connectorId;
    this.parameters = parameters;
    this.webhookUrl = webhookUrl;
    this.clientUserId = clientUserId;
    this.products = null;
  }
}
