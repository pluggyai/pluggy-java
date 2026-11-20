package ai.pluggy.client.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ai.pluggy.client.PluggyClient;
import ai.pluggy.client.request.CreateConnectTokenRequest;
import ai.pluggy.client.response.ConnectTokenResponse;
import ai.pluggy.client.response.ConnectorsResponse;
import ai.pluggy.client.response.ErrorResponse;
import ai.pluggy.client.unit.FakePluggyServer.Reply;
import ai.pluggy.exception.PluggyException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import retrofit2.Response;

/**
 * An application with an IP allowlist rejects requests from other IPs, on POST /auth and on every
 * request made with its API key, with 403 CLIENT_IP_NOT_ALLOWED. The caller must get that answer
 * (status and codeDescription), not a different error, and the client must not keep retrying.
 */
public class ClientIpNotAllowedTest {

  private static final String IP_NOT_ALLOWED = "{\"code\":403,"
    + "\"codeDescription\":\"CLIENT_IP_NOT_ALLOWED\","
    + "\"message\":\"The request IP address is not in the list of allowed IPs configured for "
    + "this application.\"}";

  private FakePluggyServer api;
  private PluggyClient client;

  @BeforeEach
  void setUp() throws Exception {
    api = new FakePluggyServer();
    // without an api key the API can't tell which application is calling
    api.on("POST", "/connect_token", request -> request.apiKey() == null
      ? Reply.json(403, "{\"code\":403,\"message\":\"Missing or invalid authorization\"}")
      : Reply.json(403, IP_NOT_ALLOWED));
    client = PluggyClient.builder()
      .baseUrl(api.url())
      .clientIdAndSecret("client-id", "client-secret")
      .build();
  }

  @AfterEach
  void tearDown() {
    api.close();
  }

  private Response<ConnectTokenResponse> createConnectToken() throws Exception {
    return client.service()
      .createConnectToken(new CreateConnectTokenRequest(null, "user-1"))
      .execute();
  }

  @Test
  void authRejected_requestReturnsTheAuthError_andIsNotSentWithoutAKey() throws Exception {
    api.on("POST", "/auth", request -> Reply.json(403, IP_NOT_ALLOWED));

    Response<ConnectTokenResponse> response = createConnectToken();

    assertEquals(403, response.code());
    ErrorResponse error = client.parseError(response);
    assertEquals(ErrorResponse.CLIENT_IP_NOT_ALLOWED, error.getCodeDescription());
    assertTrue(error.isClientIpNotAllowed());
    assertEquals(1, api.received("/auth").size());
    assertTrue(api.received("/connect_token").isEmpty(),
      "the request must not be sent without an api key");
  }

  @Test
  void authRejected_eachCallAsksForAKeyOnce() throws Exception {
    api.on("POST", "/auth", request -> Reply.json(403, IP_NOT_ALLOWED));

    for (int i = 0; i < 3; i++) {
      Response<ConnectorsResponse> response = client.service().getConnectors().execute();
      assertEquals(403, response.code());
      assertEquals("CLIENT_IP_NOT_ALLOWED", client.parseError(response).getCodeDescription());
    }

    assertEquals(3, api.received("/auth").size());
    assertTrue(api.received("/connectors").isEmpty());
  }

  @Test
  void authenticate_throwsWithStatusAndCodeDescription() {
    api.on("POST", "/auth", request -> Reply.json(403, IP_NOT_ALLOWED));

    PluggyException error = assertThrows(PluggyException.class, client::authenticate);

    assertEquals(403, error.getStatus());
    assertEquals(ErrorResponse.CLIENT_IP_NOT_ALLOWED, error.getCodeDescription());
    assertTrue(error.isClientIpNotAllowed());
    assertTrue(error.getMessage().contains("CLIENT_IP_NOT_ALLOWED"), error.getMessage());
    assertEquals(1, api.received("/auth").size());
  }

  @Test
  void apiKeyRequestRejected_isReturnedAsIs_withoutRetry() throws Exception {
    api.on("POST", "/auth",
      request -> Reply.apiKey(FakePluggyServer.jwt("team", FakePluggyServer.FAR_FUTURE)));

    Response<ConnectTokenResponse> response = createConnectToken();

    assertEquals(403, response.code());
    ErrorResponse error = client.parseError(response);
    assertEquals(ErrorResponse.CLIENT_IP_NOT_ALLOWED, error.getCodeDescription());
    assertEquals(403, (int) error.getCode());
    assertEquals(1, api.received("/auth").size());
    assertEquals(1, api.received("/connect_token").size());
  }

  @Test
  void apiKeyRequestRejected_withAKeyThatMayHaveExpired_isRetriedOnlyOnce() throws Exception {
    // a key whose expiry can't be read is refreshed once on a 403, then the answer is final
    api.on("POST", "/auth", request -> Reply.apiKey("opaque-key"));

    Response<ConnectTokenResponse> response = createConnectToken();

    assertEquals(403, response.code());
    assertEquals(ErrorResponse.CLIENT_IP_NOT_ALLOWED,
      client.parseError(response).getCodeDescription());
    assertEquals(2, api.received("/auth").size());
    assertEquals(2, api.received("/connect_token").size());
  }

  @Test
  void otherErrors_areNotClientIpNotAllowed() {
    api.on("POST", "/auth",
      request -> Reply.json(401, "{\"code\":401,\"message\":\"invalid credentials\"}"));

    PluggyException error = assertThrows(PluggyException.class, client::authenticate);

    assertEquals(401, error.getStatus());
    assertEquals(null, error.getCodeDescription());
    assertFalse(error.isClientIpNotAllowed());
    assertFalse(new ErrorResponse().isClientIpNotAllowed());
  }
}
