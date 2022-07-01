package ai.pluggy.exception;

import java.io.IOException;
import okhttp3.Response;

public class PluggyException extends Exception {

  public final String apiMessage;
  public final Integer status;

  public PluggyException(String message, Response response) {
    super(message);
    this.status = response.code();
    this.apiMessage = response.message();
  }

  public PluggyException(String message, Response response, Throwable cause) {
    super(message, cause);
    this.status = response.code();
    this.apiMessage = response.message();
  }

  public String getApiMessage() {
    return apiMessage;
  }

  public int getStatus() {
    return status;
  }
}
