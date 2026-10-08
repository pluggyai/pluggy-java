package ai.pluggy.client.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Where to redirect the user after the Smart Transfer preauthorization flow completes or ends in
 * error. Used both when creating a preauthorization and in the preauthorization response.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SmartTransferCallbackUrls {

  /** Url to redirect to after the preauthorization was completed. */
  String success;

  /** Url to redirect to after the preauthorization ended in error. */
  String error;
}
