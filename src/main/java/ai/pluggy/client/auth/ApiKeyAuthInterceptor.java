package ai.pluggy.client.auth;

import static ai.pluggy.utils.Asserts.assertNotNull;

import ai.pluggy.client.response.AuthResponse;
import ai.pluggy.exception.PluggyException;
import ai.pluggy.utils.Utils;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public class ApiKeyAuthInterceptor implements Interceptor {

  private static final Logger logger = LogManager.getLogger(ApiKeyAuthInterceptor.class);

  private static final String X_API_KEY_HEADER = "x-api-key";

  private final String clientId;
  private final String clientSecret;
  private final String authUrl;
  private final TokenProvider tokenProvider;

  public ApiKeyAuthInterceptor(String authUrl, String clientId, String clientSecret) {
    this(authUrl, clientId, clientSecret, new TokenProvider());
  }

  public ApiKeyAuthInterceptor(String authUrl, String clientId, String clientSecret,
    TokenProvider tokenProvider) {
    assertNotNull(clientId, "clientId");
    assertNotNull(clientSecret, "clientSecret");
    assertNotNull(authUrl, "authUrl");
    assertNotNull(tokenProvider, "tokenProvider");
    this.authUrl = authUrl;
    this.clientId = clientId;
    this.clientSecret = clientSecret;
    this.tokenProvider = tokenProvider;
  }

  @NotNull
  @Override
  public Response intercept(@NotNull Chain chain) throws IOException {
    Request originalRequest = chain.request();

    if (isAuthRequest(originalRequest)) {
      // already is an Auth request -> proceed -> grab apiKey response
      Response response = interceptApiKeyResponse(chain, originalRequest);
      logger.info("Stored api key response from Auth request.");
      return response;
    }
    // -> is a regular request.

    // check if 'api key' is present. If not, we need to get one first.
    String originalRequestToken = originalRequest.header(X_API_KEY_HEADER);

    String apiKey;
    if (originalRequestToken == null) {
      // No 'api key' in header -> get one from token provider.
      if (tokenProvider.getApiKey() != null) {
        apiKey = tokenProvider.getApiKey();
        logger.info("Using api key from stored tokenProvider.");
      } else {
        logger.info("Requesting new api key...");
        // No api key stored -> fetch a new one and store it.
        apiKey = authenticate(chain);
        tokenProvider.setApiKey(apiKey);
        logger.info("Auth api key response OK, storing in local tokenProvider.");
      }
    } else {
      apiKey = originalRequestToken;
    }

    Request authenticatedRequest = requestWithAuth(originalRequest, apiKey);

    // run request with 'api key', retrying once if apiKey was invalid or expired.
    return proceedWithAuthRetry(chain, authenticatedRequest);
  }

  private Request requestWithAuth(Request originalRequest, String apiKey) {
    return originalRequest.newBuilder()
      .addHeader(X_API_KEY_HEADER, apiKey)
      .addHeader("User-Agent", String.format("PluggyJava/%s",  Utils.getSdkVersion()))
      .build();
  }

  private Response proceedWithAuthRetry(@NotNull Chain chain, Request originalRequest)
    throws IOException {
    // attempt to proceed with original request
    Response response = chain.proceed(originalRequest);

    // check auth errors
    boolean isResponseAuthError = response.code() != 403 && response.code() != 401;
    if (isResponseAuthError) {
      // no auth problems -> response was OK
      return response;
    }

    // auth error -> API key expired? try to refresh API key, and retry original request.
    response.close();

    logger.info("ApiKey expired, attempting to request a new one and retry original request...");
    String apiKey = authenticate(chain);
    tokenProvider.setApiKey(apiKey);

    logger.info("ApiKey refreshed OK. Retrying original request...");
    Request authenticatedRequest = requestWithAuth(originalRequest, apiKey);
    return chain.proceed(authenticatedRequest);
  }

  @NotNull
  private Response interceptApiKeyResponse(@NotNull Chain chain, Request originalRequest)
    throws IOException {
    Response response = chain.proceed(originalRequest);
    checkAuthResponseOk(response);
    ResponseBody body = response.body();
    assertNotNull(body, "/auth response.body()");
    String responseBodyString = body.string();
    String apiKey = extractApiKeyFromResponseBody(responseBodyString);
    tokenProvider.setApiKey(apiKey);
    return response.newBuilder()
      .body(ResponseBody.create(responseBodyString, body.contentType()))
      .addHeader("User-Agent", String.format("PluggyJava/%s",  Utils.getSdkVersion()))
      .build();
  }

  private String extractApiKeyFromResponseBody(String responseBody) throws IOException {
    AuthResponse authResponse = new Gson().fromJson(responseBody, AuthResponse.class);
    return authResponse.getApiKey();
  }

  private void checkAuthResponseOk(Response response) throws PluggyException {
    if (!response.isSuccessful()) {
      throw new PluggyException(
        "Auth API request failed, code: " + response.code() + ", msg: " + response.message());
    }
  }

  private boolean isAuthRequest(Request request) {
    String url = request.url().toString();
    return url.matches(authUrl);
  }

  public String authenticate(Chain chain) throws IOException {
    Request request = buildAuthRequest(chain.request().newBuilder());

    try (Response response = chain.proceed(request)) {
      checkAuthResponseOk(response);
      ResponseBody responseBody = response.body();
      assertNotNull(responseBody, "/auth response.body()");
      return extractApiKeyFromResponseBody(responseBody.string());
    }
  }

  private Request buildAuthRequest(Request.Builder requestBuilder) {
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

    return requestBuilder
      .url(authUrl)
      .post(body)
      .addHeader("content-type", "application/json")
      .addHeader("cache-control", "no-cache")
      .addHeader("User-Agent", String.format("PluggyJava/%s",  Utils.getSdkVersion()))
      .build();
  }
}
