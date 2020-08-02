package ai.pluggy.exception;

import java.io.IOException;
import okhttp3.Response;

public class PluggyException extends IOException {

  public String apiMessage;
  public Integer status;

  public PluggyException(String message) {
    super(message);
  }


  public PluggyException(String message, Response response) {
    super(message + ", status: " + response.code() + ", API message: " + response.message());
    this.status = response.code();
    this.apiMessage = response.message();
  }

  public PluggyException(String message, Throwable cause) {
    super(message, cause);
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
