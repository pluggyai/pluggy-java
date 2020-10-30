package ai.pluggy.client.integration.util;

import ai.pluggy.client.PluggyClient;
import ai.pluggy.client.response.ErrorResponse;
import java.io.IOException;
import retrofit2.Response;

public class AssertionsUtils {

  public static void assertSuccessful(Response response, PluggyClient client) throws IOException {
    if (response.isSuccessful()) {
      return;
    }
    ErrorResponse errorResponse = client.parseError(response);
    if (errorResponse == null) {
      throw new AssertionError(
        "Expected successful response but got status=" + response.code());
    }
    throw new AssertionError(
      "Expected successful response but got status=" + response.code() + ", code=" + errorResponse
        .getCode() + ", message='"
        + errorResponse.getMessage() + "'");
  }

}
