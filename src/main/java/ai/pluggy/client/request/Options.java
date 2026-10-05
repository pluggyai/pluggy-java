package ai.pluggy.client.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Options {

  String webhookUrl;
  String clientUserId;
  String oauthRedirectUri;
  Boolean avoidDuplicates;
}
