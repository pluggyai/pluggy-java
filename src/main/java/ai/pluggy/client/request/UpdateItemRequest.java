package ai.pluggy.client.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateItemRequest {
  Integer connectorId;
  @ToString.Exclude
  ParametersMap parameters;
  String webhookUrl;
  String clientUserId;
  ProductType[] products;
}
