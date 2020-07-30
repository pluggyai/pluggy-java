package ai.pluggy.client;

import ai.pluggy.exception.PluggyException;
import ai.pluggy.model.AuthResponse;
import com.google.gson.Gson;
import java.io.IOException;
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
  private String apiKey;

  public PluggyClient() throws PluggyException {

  }

  public void authenticate(String clientId, String clientSecret) throws IOException {
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
      this.apiKey = authResponse.getApiKey();
    }
  }
}
