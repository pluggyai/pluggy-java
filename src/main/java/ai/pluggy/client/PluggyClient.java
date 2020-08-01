package ai.pluggy.client;

import static ai.pluggy.utils.Asserts.assertNotNull;
import static ai.pluggy.utils.Utils.formatQueryParams;

import ai.pluggy.client.request.ConnectorsSearchRequest;
import ai.pluggy.client.response.AuthResponse;
import ai.pluggy.client.response.ConnectorsResponse;
import ai.pluggy.exception.PluggyException;
import ai.pluggy.utils.Asserts;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public final class PluggyClient {

  private static final Logger logger = LogManager.getLogger(PluggyClient.class);
  private static final long API_KEY_EXPIRE_TIME = 2 * 60 * 1000; // api key expires in 2 hours

  private String baseUrl = "https://api.pluggy.ai";
  private String authUrl = this.baseUrl + "/auth";
  private String getConnectorsUrl = this.baseUrl + "/connectors";

  private String clientId;
  private String clientSecret;

  private String apiKey;
  private Date apiKeyExpireDate;
  private OkHttpClient httpClient;

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

  /**
   * Provides an API to build a PluggyClient instance while ensuring all parameters are defined and valid.
   */
  public static class PluggyClientBuilder {

    private static String BASE_URL = "https://api.pluggy.ai";
    private static Integer DEFAULT_HTTP_CONNECT_TIMEOUT_SECONDS = 10;
    private static Integer DEFAULT_HTTP_READ_TIMEOUT_SECONDS = 180;

    private String clientId;
    private String clientSecret;

    public PluggyClientBuilder clientIdAndSecret(String clientId, String clientSecret) {
      assertNotNull(clientId, "client id");
      assertNotNull(clientId, "secret");
      this.clientId = clientId;
      this.clientSecret = clientSecret;
      return this;
    }

    private OkHttpClient buildOkHttpClient() {
      OkHttpClient httpClient = new OkHttpClient.Builder()
        .readTimeout(DEFAULT_HTTP_READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .connectTimeout(DEFAULT_HTTP_CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .build();

      return httpClient;
    }

    public PluggyClient build() {
      if (clientId == null || clientSecret == null) {
        throw new IllegalArgumentException("Must set a clientId and secret.");
      }

      PluggyClient pluggyClient = new PluggyClient();
      pluggyClient.setClientId(clientId);
      pluggyClient.setClientSecret(clientSecret);
      pluggyClient.setBaseUrl(BASE_URL);
      pluggyClient.setHttpClient(buildOkHttpClient());

      return pluggyClient;
    }
  }

  public void authenticate() throws IOException {
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
      .url(this.authUrl)
      .post(body)
      .addHeader("content-type", "application/json")
      .addHeader("cache-control", "no-cache")
      .build();

    AuthResponse authResponse;

    try (Response response = this.httpClient.newCall(request).execute()) {
      if (!response.isSuccessful()) {
        throw new PluggyException(
          "Pluggy Auth request failed, status: " + response.code() + ", message: " + response
            .message());
      }

      ResponseBody responseBody = response.body();
      assertNotNull(responseBody, "response.body()");
      authResponse = gson.fromJson(responseBody.string(), AuthResponse.class);
    }
    this.apiKey = authResponse.getApiKey();
    this.apiKeyExpireDate = new Date(new Date().getTime() + API_KEY_EXPIRE_TIME);
  }

  /**
   * GET /connectors request - retrieve all results
   *
   * @return connectorResponse
   */
  public ConnectorsResponse getConnectors() throws IOException {
    return getConnectors(new ConnectorsSearchRequest());
  }

  /**
   * GET /connectors request with search params
   *
   * @param connectorSearch - search params such as "name", "countries" and "types"
   * @return connectorResponse
   */
  public ConnectorsResponse getConnectors(ConnectorsSearchRequest connectorSearch)
    throws IOException {
    ensureAuthenticated();

    String queryString = formatQueryParams(connectorSearch);
    String urlString = this.getConnectorsUrl + queryString;

    Request request = new Request.Builder()
      .url(urlString)
      .addHeader("content-type", "application/json")
      .addHeader("x-api-key", apiKey)
      .build();

    ConnectorsResponse connectorsResponse;

    try (Response response = this.httpClient.newCall(request).execute()) {
      if (!response.isSuccessful()) {
        throw new PluggyException(
          "Pluggy GET connectors request failed, status: " + response.code() + ", message: "
            + response
            .message());
      }
      ResponseBody responseBody = response.body();
      Asserts.assertNotNull(responseBody, "response.body()");

      connectorsResponse = new Gson().fromJson(responseBody.string(), ConnectorsResponse.class);
    }

    return connectorsResponse;
  }


  /**
   * Helper method that retrieves/refreshes API key if not present or expired.
   */
  private void ensureAuthenticated() throws IOException {
    Date now = new Date();

    if (this.apiKey == null) {
      logger.info("Not authenticated, authenticating first...");
      this.authenticate();
      logger.info("Auth OK!");
      return;
    }
    if (now.getTime() > this.apiKeyExpireDate.getTime()) {
      logger.info("API key expired, requesting a new token...");
      this.authenticate();
      logger.info("Auth Token refresh OK!");
    }
  }
}
