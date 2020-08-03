package ai.pluggy.client;

import static ai.pluggy.utils.Asserts.assertNotNull;

import ai.pluggy.client.auth.ApiKeyAuthInterceptor;
import ai.pluggy.client.auth.TokenProvider;
import ai.pluggy.client.response.AuthResponse;
import ai.pluggy.client.response.ErrorResponse;
import ai.pluggy.exception.PluggyException;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class PluggyClient {

  private static final Logger logger = LogManager.getLogger(PluggyClient.class);

  private String baseUrl;
  private String authUrlPath = "/auth";

  private String clientId;
  private String clientSecret;

  private OkHttpClient httpClient;
  private PluggyApiService service;

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

  public void setHttpClient(OkHttpClient httpClient) {
    this.httpClient = httpClient;
  }

  public void setService(PluggyApiService service) {
    this.service = service;
  }

  public String getBaseUrl() {
    return baseUrl;
  }

  public OkHttpClient getHttpClient() {
    return httpClient;
  }

  public ErrorResponse parseError(retrofit2.Response responseWithError) throws IOException {
    if (responseWithError.isSuccessful()) {
      throw new IllegalStateException(
        "Response is successful, can't be parsed as an error response!");
    }

    ResponseBody errorResponseBody = responseWithError.errorBody();
    if (errorResponseBody == null) {
      return null;
    }

    String errorResponseJson;
    try {
      errorResponseJson = errorResponseBody.string();
    } catch (IOException e) {
      throw new IOException("Could not convert error response to string", e);
    }

    ErrorResponse errorResponse = new Gson().fromJson(errorResponseJson, ErrorResponse.class);

    return errorResponse;
  }

  /**
   * Provides an API to build a PluggyClient instance while ensuring all parameters are defined and valid.
   */
  public static class PluggyClientBuilder {

    private static String BASE_URL = "https://api.pluggy.ai";

    private static Integer DEFAULT_HTTP_CONNECT_TIMEOUT_SECONDS = 10;
    private static Integer DEFAULT_HTTP_READ_TIMEOUT_SECONDS = 180;

    private String authPath = "/auth";
    private String clientId;
    private String clientSecret;

    public PluggyClientBuilder clientIdAndSecret(String clientId, String clientSecret) {
      assertNotNull(clientId, "client id");
      assertNotNull(clientSecret, "secret");
      this.clientId = clientId;
      this.clientSecret = clientSecret;
      return this;
    }

    private OkHttpClient buildOkHttpClient() {
      String authUrlPath = BASE_URL + authPath;

      OkHttpClient httpClient = new OkHttpClient.Builder()
        .readTimeout(DEFAULT_HTTP_READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .connectTimeout(DEFAULT_HTTP_CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .addInterceptor(
          new ApiKeyAuthInterceptor(authUrlPath, clientId, clientSecret, new TokenProvider()))
        .build();
      return httpClient;
    }

    private Gson buildGson() {
      return new GsonBuilder()
        .setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
        .create();
    }

    public PluggyClient build() {
      if (clientId == null || clientSecret == null) {
        throw new IllegalArgumentException("Must set a clientId and secret.");
      }

      OkHttpClient httpClient = buildOkHttpClient();
      Retrofit retrofit = new Retrofit.Builder()
        .baseUrl(BASE_URL)
        .validateEagerly(true)
        .addConverterFactory(GsonConverterFactory.create(buildGson()))
        .client(httpClient)
        .build();

      PluggyApiService pluggyRetrofitApiService = retrofit.create(PluggyApiService.class);

      PluggyClient pluggyClient = new PluggyClient();
      pluggyClient.setClientId(clientId);
      pluggyClient.setClientSecret(clientSecret);
      pluggyClient.setBaseUrl(BASE_URL);
      pluggyClient.setHttpClient(httpClient);
      pluggyClient.setService(pluggyRetrofitApiService);

      return pluggyClient;
    }
  }

  public String authenticate() throws IOException {
    if (clientId == null || clientSecret == null) {
      throw new IllegalStateException(
        "Invalid state, both clientId and clientSecret must be defined!");
    }

    Map<String, String> parameters = new HashMap<>();
    parameters.put("clientId", clientId);
    parameters.put("clientSecret", clientSecret);

    MediaType mediaType = MediaType.parse("application/json");
    Gson gson = new Gson();
    String jsonBody = gson.toJson(parameters);

    RequestBody body = RequestBody.create(jsonBody, mediaType);

    Request request = new Request.Builder()
      .url(this.baseUrl + this.authUrlPath)
      .post(body)
      .addHeader("content-type", "application/json")
      .addHeader("cache-control", "no-cache")
      .build();

    AuthResponse authResponse;

    try (okhttp3.Response response = this.httpClient.newCall(request).execute()) {
      if (!response.isSuccessful()) {
        throw new PluggyException(
          "Pluggy Auth request failed, status: " + response.code() + ", message: " + response
            .message());
      }

      ResponseBody responseBody = response.body();
      assertNotNull(responseBody, "response.body()");
      authResponse = gson.fromJson(responseBody.string(), AuthResponse.class);
    }
    return authResponse.getApiKey();
  }

  public PluggyApiService service() {
    return this.service;
  }

}
