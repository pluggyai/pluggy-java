package ai.pluggy.exception;

import ai.pluggy.client.response.ErrorResponse;
import okhttp3.Response;

public class PluggyException extends Exception {

  public final String apiMessage;
  public final Integer status;
  /**
   * The {@code codeDescription} of the API's error body, or null when the response had none.
   */
  public final String codeDescription;

  public PluggyException(String message, Response response) {
    this(message, response, null, null);
  }

  public PluggyException(String message, Response response, Throwable cause) {
    this(message, response, cause, null);
  }

  public PluggyException(String message, Response response, Throwable cause,
    String codeDescription) {
    super(message, cause);
    this.status = response.code();
    this.apiMessage = response.message();
    this.codeDescription = codeDescription;
  }

  public String getApiMessage() {
    return apiMessage;
  }

  public int getStatus() {
    return status;
  }

  public String getCodeDescription() {
    return codeDescription;
  }

  /**
   * Whether the API rejected the request because its IP is not in the application's IP allowlist.
   */
  public boolean isClientIpNotAllowed() {
    return ErrorResponse.CLIENT_IP_NOT_ALLOWED.equals(codeDescription);
  }
}
