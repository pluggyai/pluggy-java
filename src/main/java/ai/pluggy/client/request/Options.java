package ai.pluggy.client.request;

import lombok.AllArgsConstructor;
import lombok.Builder;

@Builder
@AllArgsConstructor
public class Options {

  String webhookUrl;
  String clientUserId;
}
