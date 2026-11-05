package ai.pluggy.client.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ai.pluggy.client.PluggyClient;
import ai.pluggy.client.response.ConnectorsResponse;
import ai.pluggy.client.response.ErrorResponse;
import ai.pluggy.client.unit.FakePluggyServer.Reply;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import retrofit2.Response;

/**
 * The interceptor reads an issued key's exp claim only to decide when to ask for a new one, so a
 * key it can't read must not break the client, and a failed refresh must leave the API's answer
 * readable.
 */
public class ApiKeyRefreshTest {

  private static final String OK = "{\"results\":[]}";

  private FakePluggyServer api;
  private String acceptedKey;

  @BeforeEach
  void setUp() throws Exception {
    api = new FakePluggyServer();
    api.on("GET", "/connectors", request -> request.apiKey() != null
      && request.apiKey().equals(acceptedKey)
      ? Reply.json(200, OK)
      : Reply.json(401, "{\"message\":\"api key expired\",\"code\":401}"));
  }

  @AfterEach
  void tearDown() {
    api.close();
  }

  private PluggyClient client() {
    return PluggyClient.builder()
      .baseUrl(api.url())
      .clientIdAndSecret("client-id", "client-secret")
      .build();
  }

  private static String tokenWithPayload(String payloadJson) {
    return "header." + Base64.getUrlEncoder().withoutPadding()
      .encodeToString(payloadJson.getBytes(StandardCharsets.UTF_8)) + ".signature";
  }

  @Test
  void keyWithNonStringClaims_isReusedUntilItExpires() throws Exception {
    acceptedKey = tokenWithPayload("{\"exp\":4102444800,\"scopes\":[\"read\"]}");
    api.on("POST", "/auth", request -> Reply.apiKey(acceptedKey));
    PluggyClient client = client();

    assertEquals(200, client.service().getConnectors().execute().code());
    assertEquals(200, client.service().getConnectors().execute().code());

    assertEquals(1, api.received("/auth").size());
  }

  @Test
  void keyThatIsNotAJwt_isReusedUntilRejected_thenRefreshedOnce() throws Exception {
    AtomicInteger authCalls = new AtomicInteger();
    api.on("POST", "/auth", request -> Reply.apiKey("opaque-" + authCalls.incrementAndGet()));
    acceptedKey = "opaque-1";
    PluggyClient client = client();

    assertEquals(200, client.service().getConnectors().execute().code());
    assertEquals(200, client.service().getConnectors().execute().code());
    acceptedKey = "opaque-2";
    assertEquals(200, client.service().getConnectors().execute().code());

    assertEquals(2, authCalls.get());
    assertEquals(Arrays.asList("opaque-1", "opaque-1", "opaque-1", "opaque-2"),
      api.apiKeysSentTo("/connectors"));
  }

  @Test
  void failedRefresh_returnsTheRejectionReadable() throws Exception {
    AtomicInteger authCalls = new AtomicInteger();
    api.on("POST", "/auth", request -> authCalls.incrementAndGet() == 1
      ? Reply.apiKey(FakePluggyServer.jwt("team", 1L))
      : Reply.json(500, "{}"));
    PluggyClient client = client();

    Response<ConnectorsResponse> response = client.service().getConnectors().execute();

    assertEquals(401, response.code());
    ErrorResponse error = client.parseError(response);
    assertEquals("api key expired", error.getMessage());
  }
}
