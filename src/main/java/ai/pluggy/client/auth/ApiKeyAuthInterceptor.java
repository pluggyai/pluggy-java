package ai.pluggy.client.auth;

import static ai.pluggy.utils.Asserts.assertNotNull;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.reflect.TypeToken;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

  /**
   * Helper to check if JWT token apiKey is expired
   *
   * @param token
   */
  private boolean isApiKeyExpired(String token) {
    Base64.Decoder decoder = Base64.getUrlDecoder();
    String[] parts = token.split("\\."); // Splitting header, payload and signature
    String jwtHeader = new String(decoder.decode(parts[1])); // extracting payload part

    Map<String, String> parsedJwt = new Gson().fromJson(jwtHeader,
      new TypeToken<Map<String, String>>() {
      }.getType());

    long expiresAtInSeconds = Long.parseLong(parsedJwt.get("exp"));
    long currentTimeInSeconds = (long) Math.floor((double) (new Date().getTime() / 1000));

    return currentTimeInSeconds >= expiresAtInSeconds;
  }

  @NotNull
  @Override
  public Response intercept(@NotNull Chain chain) throws IOException {
    Request originalRequest = chain.request();

    if (isAuthRequest(originalRequest)) {
      // already is an Auth request -> proceed -> grab apiKey response
      Response authResponse = chain.proceed(originalRequest);
      interceptApiKeyResponse(authResponse);
      return authResponse;
    }
    // -> is a regular request.

    // check if 'api key' is present. If not, we need to get one first.
    String originalRequestToken = originalRequest.header(X_API_KEY_HEADER);

    String apiKey;
    if (originalRequestToken == null) {
      // No 'api key' in header -> get one from token provider.
      if (tokenProvider.getApiKey() != null && !isApiKeyExpired(tokenProvider.getApiKey())) {
        apiKey = tokenProvider.getApiKey();
        logger.info("Using api key from stored tokenProvider.");
      } else {
        logger.info("Requesting new api key...");
        // No api key stored -> fetch a new one and store it.
        apiKey = authenticateInChain(chain);
      }
    } else {
      apiKey = originalRequestToken;
    }

    if (apiKey == null) {
      // could not renew apiKey, just proceed with original request flow
      return chain.proceed(originalRequest);
    }

    Request authenticatedRequest = requestWithAuth(originalRequest, apiKey);

    // run request with 'api key', retrying once if apiKey was invalid or expired.
    return proceedWithAuthRetry(chain, authenticatedRequest);
  }

  private Request requestWithAuth(Request originalRequest, String apiKey) {
    // override the apiKey of the original request with the new one
    return originalRequest.newBuilder()
      .header(X_API_KEY_HEADER, apiKey)
      // TOOD: add dynamic version
      .header("User-Agent", "PluggyJava/0.16.2")
      .build();
  }

  private Response proceedWithAuthRetry(@NotNull Chain chain, Request originalRequest)
    throws IOException {
    // attempt to proceed with original request
    Response response = chain.proceed(originalRequest);

    // check auth errors
    boolean isResponseAuthError = response.code() == 403 || response.code() == 401;

    if (!isResponseAuthError) {
      // no auth problems -> response was OK
      return response;
    }

    // auth error response, check if it was due to expired token
    String apiKey = originalRequest.header(X_API_KEY_HEADER);
    boolean isApiKeyExpired = apiKey != null && this.isApiKeyExpired(apiKey);

    if (!isApiKeyExpired) {
      // apiKey not expired, auth error is unrelated to it -> return error response
      return response;
    }

    // apiKey expired, try to refresh API key, and retry original request.
    response.close();

    logger.info("ApiKey expired, attempting to request a new one and retry original request...");

    String newApiKey = authenticateInChain(chain);

    if (newApiKey == null) {
      logger.info("ApiKey could not be refreshed.");
      return response;
    }

    logger.info("ApiKey refreshed OK. Retrying original request...");
    Request authenticatedRequest = requestWithAuth(originalRequest, newApiKey);
    return chain.proceed(authenticatedRequest);
  }

  @Nullable
  private String authenticateInChain(@NotNull Chain chain) throws IOException {
    Request authRequest = buildAuthRequest(chain.request().newBuilder());
    String newApiKey;
    try (Response authResponse = chain.proceed(authRequest)) {
      newApiKey = interceptApiKeyResponse(authResponse);
    }
    return newApiKey;
  }

  private String interceptApiKeyResponse(Response response) {
    String apiKey;

    try {
      apiKey = AuthenticationHelper.extractApiKeyFromResponse(response);
    } catch (Exception e) {
      logger.error(
        String.format("Could not extract api key response from Auth request: [%s] %s",
          e.getClass().toString(),
          e.getMessage()));
      return null;
    }

    if (apiKey == null) {
      logger.error(
        "Parsed api key response from Auth request but it was null, this could mean that the "
          + "Pluggy API has changed, please check the docs and/or consider updating pluggy-java "
          + "SDK to the latest version");
      return null;
    }

    // could extract apiKey, store it and return the original response
    tokenProvider.setApiKey(apiKey);
    logger.info("Stored api key response from Auth request.");

    return apiKey;
  }

  private boolean isAuthRequest(Request request) {
    String url = request.url().toString();
    return url.matches(authUrl);
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
      .build();
  }
}
