package ai.pluggy.client.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor
public class CreateConnectTokenRequest {

  String itemId;
  Options options;

  public CreateConnectTokenRequest(String webhookUrl, String clientUserId) {
    this.itemId = null;
    this.options = new Options(webhookUrl, clientUserId);
  }

  public CreateConnectTokenRequest(String itemId, String webhookUrl, String clientUserId) {
    this.itemId = itemId;
    this.options = new Options(webhookUrl, clientUserId);
  }
}
