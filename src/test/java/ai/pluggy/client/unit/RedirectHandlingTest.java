package ai.pluggy.client.unit;

import static ai.pluggy.client.unit.FakePluggyServer.FAR_FUTURE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ai.pluggy.client.PluggyClient;
import ai.pluggy.client.PluggyClient.PluggyClientBuilder;
import ai.pluggy.client.request.CreateConnectTokenRequest;
import ai.pluggy.client.request.CreateItemRequest;
import ai.pluggy.client.request.ParametersMap;
import ai.pluggy.client.response.ConnectTokenResponse;
import ai.pluggy.client.response.ConnectorsResponse;
import ai.pluggy.client.unit.FakePluggyServer.Reply;
import ai.pluggy.exception.PluggyException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import retrofit2.Response;

/**
 * OkHttp removes only the Authorization header on a cross-origin redirect and keeps the body on 307
 * and 308, so following a redirect could hand x-api-key or the client secret to another host.
 */
public class RedirectHandlingTest {

  private static final String ISSUED_KEY = FakePluggyServer.jwt("team", FAR_FUTURE);

  private FakePluggyServer api;
  private FakePluggyServer otherOrigin;

  @BeforeEach
  void setUp() throws Exception {
    api = new FakePluggyServer();
    otherOrigin = new FakePluggyServer();
    api.on("POST", "/auth", request -> Reply.apiKey(ISSUED_KEY));
  }

  @AfterEach
  void tearDown() {
    api.close();
    otherOrigin.close();
  }

  private PluggyClientBuilder builder() {
    return PluggyClient.builder().baseUrl(api.url()).clientIdAndSecret("client-id", "client-secret");
  }

  @Test
  void apiRequestRedirectedOffOrigin_doesNotForwardTheKey() throws Exception {
    api.on("GET", "/connectors",
      request -> Reply.redirect(302, otherOrigin.url() + "/connectors"));

    Response<ConnectorsResponse> response = builder().build().service().getConnectors().execute();

    assertEquals(302, response.code());
    assertTrue(otherOrigin.received().isEmpty());
  }

  @Test
  void authenticateRedirectedOffOrigin_doesNotResendTheSecret() {
    api.on("POST", "/auth", request -> Reply.redirect(307, otherOrigin.url() + "/auth"));
    PluggyClient client = builder().build();

    assertThrows(PluggyException.class, client::authenticate);

    assertTrue(otherOrigin.received().isEmpty());
  }

  @Test
  void automaticAuthRedirectedOffOrigin_doesNotResendTheSecret() throws Exception {
    api.on("POST", "/auth", request -> Reply.redirect(307, otherOrigin.url() + "/auth"));
    api.on("GET", "/connectors", request -> Reply.json(401, "{}"));

    builder().build().service().getConnectors().execute();

    assertTrue(otherOrigin.received().isEmpty());
  }

  @Test
  void itemCreationRedirectedOffOrigin_doesNotResendTheCredentials() throws Exception {
    api.on("POST", "/items", request -> Reply.redirect(307, otherOrigin.url() + "/items"));

    builder().build().service()
      .createItem(new CreateItemRequest(0, ParametersMap.map("password", "p")))
      .execute();

    assertTrue(otherOrigin.received().isEmpty());
  }

  @Test
  void callerCanStillOptInToRedirects() throws Exception {
    api.on("GET", "/connectors", request -> Reply.redirect(302, "/moved"));
    api.on("GET", "/moved", request -> Reply.json(200, "{\"results\":[]}"));
    PluggyClientBuilder builder = builder();
    builder.okHttpClientBuilder().followRedirects(true);

    Response<ConnectorsResponse> response = builder.build().service().getConnectors().execute();

    assertEquals(200, response.code());
  }

  @Test
  void createConnectToken_worksWithoutRelyingOnTheLegacyRedirect() throws Exception {
    // the deprecated route only answers with a redirect to the current one
    api.on("POST", "/connecttokens", request -> Reply.redirect(308, "/connect_token"));
    api.on("POST", "/connect_token",
      request -> Reply.json(200, "{\"accessToken\":\"connect-token\"}"));

    Response<ConnectTokenResponse> response = builder().build().service()
      .createConnectToken(new CreateConnectTokenRequest("https://example.com/hook", "user-1"))
      .execute();

    assertEquals("connect-token", response.body().getAccessToken());
    assertTrue(api.received("/connecttokens").isEmpty());
  }
}
