package ai.pluggy.client.auth;

import ai.pluggy.client.PluggyClient;
import ai.pluggy.client.response.AuthResponse;
import ai.pluggy.exception.PluggyException;
import ai.pluggy.utils.Utils;
import com.google.gson.JsonSyntaxException;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.IOException;

import static ai.pluggy.utils.Asserts.assertNotNull;

public class AuthenticationHelper {
  
  /**
   * Extract the apiKey from the POST /auth response. If the response is invalid or malformed, throw
   * an error instead.
   *
   * @param response - the 'POST /auth' response object
   * @return apiKey string if could be parsed correctly, null if not
   * @throws IllegalStateException if response is not successful
   * @throws IOException           if response is malformed or could not be parsed correctly
   * @throws JsonSyntaxException   if response body is not a JSON, or it is malformed
   */
  public static String extractApiKeyFromResponse(Response response)
    throws IOException, JsonSyntaxException, PluggyException {
    if (!response.isSuccessful()) {
      // response is invalid due to not being successful
      throw new PluggyException("Response was non-successful", response);
    }

    ResponseBody responseBody = response.peekBody(PluggyClient.ONE_MIB_BYTES);
    assertNotNull(responseBody, "/auth response.body()");

    String responseBodyString;
    try {
      responseBodyString = responseBody.string();
    } catch (IOException e) {
      throw new IOException("Unexpected IOException accessing response.body().string()", e);
    }

    AuthResponse authResponse;
    try {
      authResponse = Utils.parseJsonResponse(responseBodyString, AuthResponse.class);
    } catch (JsonSyntaxException exception) {
      // response didn't contain a valid JSON body, nothing to parse
      throw new JsonSyntaxException(
        String.format("/auth response is not a valid JSON body: '%s'", responseBodyString),
        exception);
    }

    return authResponse.getApiKey();
  }

}
