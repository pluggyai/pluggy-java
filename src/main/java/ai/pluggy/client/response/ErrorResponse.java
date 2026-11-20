package ai.pluggy.client.response;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ErrorResponse {

  /**
   * {@code codeDescription} of the 403 returned when the application has an IP allowlist
   * ("Allowed IPs" in the Dashboard) and the request comes from an IP that is not on it. It applies
   * to {@code POST /auth} and to every request made with the application's API key. See
   * https://docs.pluggy.ai/docs/security/ip-allowlist
   */
  public static final String CLIENT_IP_NOT_ALLOWED = "CLIENT_IP_NOT_ALLOWED";

  String message;
  Integer code;
  /**
   * Machine-readable error identifier, when the API provides one (e.g.
   * {@code LIST_ITEMS_FEATURE_NOT_ENABLED}, {@link #CLIENT_IP_NOT_ALLOWED}). Null for errors that
   * don't carry it.
   */
  String codeDescription;
  List<ErrorDetail> details;

  /**
   * Whether the request was rejected because its IP is not in the application's IP allowlist.
   */
  public boolean isClientIpNotAllowed() {
    return CLIENT_IP_NOT_ALLOWED.equals(codeDescription);
  }
}
