package ai.pluggy.client;

import static ai.pluggy.utils.Assertions.assertNotNull;

import ai.pluggy.client.response.AuthResponse;
import ai.pluggy.exception.PluggyException;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public final class PluggyClient {

  private String baseUrl = "https://api.pluggy.ai";
  private String authUrl = this.baseUrl + "/auth";

  private String clientId;
  private String clientSecret;

  private String apiKey;

  /**
   * Can't be instantiated directly - use PluggyClient.builder() instead.
   */
  private PluggyClient() {

  }

  public static PluggyClientBuilder builder() {
    return new PluggyClientBuilder();
  }

  public void setClientId(String clientId) {
    this.clientId = clientId;
  }

  public void setClientSecret(String clientSecret) {
    this.clientSecret = clientSecret;
  }

  public void setBaseUrl(String baseUrl) {
    this.baseUrl = baseUrl;
  }

  /**
   * Provides an API to build a PluggyClient instance while ensuring all parameters are defined and valid.
   */
  public static class PluggyClientBuilder {

    private static String BASE_URL = "https://api.pluggy.ai";

    private String clientId;
    private String clientSecret;

    public PluggyClient build() {
      if (clientId == null || clientSecret == null) {
        throw new IllegalArgumentException("Must set a clientId and secret.");
      }

      PluggyClient pluggyClient = new PluggyClient();
      pluggyClient.setClientId(clientId);
      pluggyClient.setClientSecret(clientSecret);
      pluggyClient.setBaseUrl(BASE_URL);
      return pluggyClient;
    }

    public PluggyClientBuilder clientIdAndSecret(String clientId, String clientSecret) {
      assertNotNull(clientId, "client id");
      assertNotNull(clientId, "secret");
      this.clientId = clientId;
      this.clientSecret = clientSecret;
      return this;
    }
  }

  public void authenticate() throws IOException {
    if (clientId == null || clientSecret == null) {
      throw new IllegalStateException(
        "Invalid state, both clientId and clientSecret must be defined!");
    }

    OkHttpClient client = new OkHttpClient();
    Map<String, String> parameters = new HashMap<>();
    parameters.put("clientId", clientId);
    parameters.put("clientSecret", clientSecret);

    MediaType mediaType = MediaType.parse("application/json");
    Gson gson = new Gson();
    String jsonBody = gson.toJson(parameters);

    RequestBody body = RequestBody.create(jsonBody, mediaType);

    Request request = new Request.Builder()
      .url(this.authUrl)
      .post(body)
      .addHeader("content-type", "application/json")
      .addHeader("cache-control", "no-cache")
      .build();

    AuthResponse authResponse;

    try (Response response = client.newCall(request).execute()) {
      if (!response.isSuccessful()) {
        throw new PluggyException(
          "Pluggy Auth request failed, status: " + response.code() + ", message: " + response
            .message());
      }

      authResponse = gson.fromJson(response.body().string(), AuthResponse.class);
    }
    this.apiKey = authResponse.getApiKey();
  }
}
