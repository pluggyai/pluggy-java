package ai.pluggy.client;

import static ai.pluggy.utils.Asserts.assertNotNull;
import static ai.pluggy.utils.Asserts.assertValidUrl;

import ai.pluggy.client.auth.ApiKeyAuthInterceptor;
import ai.pluggy.client.response.AuthResponse;
import ai.pluggy.client.response.ErrorResponse;
import ai.pluggy.exception.PluggyException;
import ai.pluggy.utils.Utils;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class PluggyClient {

  public static String AUTH_URL_PATH = "/auth";
  private String baseUrl;

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

    private static String DEFAULT_BASE_URL = "https://api.pluggy.ai";

    private static Integer DEFAULT_HTTP_CONNECT_TIMEOUT_SECONDS = 10;
    private static Integer DEFAULT_HTTP_READ_TIMEOUT_SECONDS = 180;

    private String clientId;
    private String clientSecret;
    private String baseUrl;
    private Builder okHttpClientBuilder;
    private boolean disableDefaultAuthInterceptor = false;

    private PluggyClientBuilder() {
      // init OkHttpClient.Builder instance, to expose it for configurability
      this.okHttpClientBuilder = new Builder()
        .readTimeout(DEFAULT_HTTP_READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .connectTimeout(DEFAULT_HTTP_CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS);
    }

    public PluggyClientBuilder clientIdAndSecret(String clientId, String clientSecret) {
      assertNotNull(clientId, "clientId");
      assertNotNull(clientSecret, "clientSecret");
      this.clientId = clientId;
      this.clientSecret = clientSecret;
      return this;
    }

    public PluggyClientBuilder baseUrl(String baseUrl) {
      assertNotNull(baseUrl, "baseUrl");
      this.baseUrl = baseUrl;
      return this;
    }

    /**
     * Opt-out from provided default ApiKeyAuthInterceptor, which takes care of apiKey authorization,
     * by requesting a new apiKey token when it's not set, or by reactively refreshing an existing
     * one and retrying a request in case of 401 or 403 unauthorized error responses.
     *
     * In case of opt-out, the client will have to provide it's own auth interceptor implementation,
     * which has to take care of including the "x-api-key" auth header to each http request.
     */
    public PluggyClientBuilder noAuthInterceptor() {
      this.disableDefaultAuthInterceptor = true;
      return this;
    }

    /**
     * Provides access to the OkHttpClient.Builder instance,
     * for more complex builds and configurations (interceptors, SSL, etc.)
     */
    public OkHttpClient.Builder okHttpClientBuilder() {
      return okHttpClientBuilder;
    }

    private OkHttpClient buildOkHttpClient(String baseUrl) {
      if (!disableDefaultAuthInterceptor) {
        // use ApiKeyAuthInterceptor, unless decided to opt-out and use your own
        // auth header interceptor implementation.
        String authUrlPath = baseUrl + PluggyClient.AUTH_URL_PATH;
        this.okHttpClientBuilder
          .addInterceptor(new ApiKeyAuthInterceptor(authUrlPath, clientId, clientSecret));
      }

      return okHttpClientBuilder.build();
    }

    private Gson buildGson() {
      return new GsonBuilder()
        .setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
        .create();
    }

    public PluggyClient build() {
      if (clientId == null || clientSecret == null) {
        throw new IllegalArgumentException("Must set a clientId and clientSecret.");
      }

      if (baseUrl == null) {
        baseUrl = DEFAULT_BASE_URL;
        assertValidUrl(baseUrl, "DEFAULT_BASE_URL");
      } else {
        assertValidUrl(baseUrl, "baseUrl");
      }

      OkHttpClient httpClient = buildOkHttpClient(baseUrl);

      Retrofit retrofit = new Retrofit.Builder()
        .baseUrl(baseUrl)
        .validateEagerly(true)
        .addConverterFactory(GsonConverterFactory.create(buildGson()))
        .client(httpClient)
        .build();

      PluggyApiService pluggyRetrofitApiService = retrofit.create(PluggyApiService.class);

      PluggyClient pluggyClient = new PluggyClient();
      pluggyClient.setClientId(clientId);
      pluggyClient.setClientSecret(clientSecret);
      pluggyClient.setBaseUrl(baseUrl);
      pluggyClient.setHttpClient(httpClient);
      pluggyClient.setService(pluggyRetrofitApiService);

      return pluggyClient;
    }
  }

  /**
   * Request a new apiKey from API using defined clientId & clientSecret.
   */
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
      .url(this.baseUrl + AUTH_URL_PATH)
      .post(body)
      .addHeader("content-type", "application/json")
      .addHeader("cache-control", "no-cache")
      .addHeader("User-Agent", String.format("PluggyJava/%s", Utils.getSdkVersion()))
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
