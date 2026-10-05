package ai.pluggy.client.response;

import java.util.Date;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * User action the item is waiting on (for example, scanning a QR code or authorizing access in the institution's app).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemUserAction {

  /** Instructions related to the user action. */
  String instructions;

  /** Additional information related to the user action, for example in a device authorization flow. */
  Map<String, Object> attributes;

  /** User action expiration date. */
  Date expiresAt;

  /** Kind of action the user has to take: {@code qr} or {@code authorize-access}. */
  String type;
}
