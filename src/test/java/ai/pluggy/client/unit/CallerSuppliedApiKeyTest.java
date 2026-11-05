package ai.pluggy.client.unit;

import static ai.pluggy.client.unit.FakePluggyServer.FAR_FUTURE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ai.pluggy.client.PluggyClient;
import ai.pluggy.client.PluggyClient.PluggyClientBuilder;
import ai.pluggy.client.response.ConnectorsResponse;
import ai.pluggy.client.unit.FakePluggyServer.Reply;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import retrofit2.Response;

/**
 * An x-api-key already on the request is the caller's choice of credential. The interceptor must
 * never swap it for the team key, which only the keys it issued itself may be refreshed to.
 */
public class CallerSuppliedApiKeyTest {

  private static final String ISSUED_KEY = FakePluggyServer.jwt("team", FAR_FUTURE);
  /** Unsigned token whose payload is {"exp":1}, written out so it doesn't depend on any helper. */
  private static final String EXPIRED_CLAIM_KEY = "a.eyJleHAiOjF9.c";

  private FakePluggyServer api;
  private String acceptedKey;

  @BeforeEach
  void setUp() throws Exception {
    api = new FakePluggyServer();
    acceptedKey = ISSUED_KEY;
    api.on("POST", "/auth", request -> Reply.apiKey(ISSUED_KEY));
    api.on("GET", "/connectors", request -> acceptedKey.equals(request.apiKey())
      ? Reply.json(200, "{\"results\":[]}")
      : Reply.json(401, "{}"));
  }

  @AfterEach
  void tearDown() {
    api.close();
  }

  private PluggyClient clientSending(String apiKey) {
    PluggyClientBuilder builder = PluggyClient.builder()
      .baseUrl(api.url())
      .clientIdAndSecret("client-id", "client-secret");
    builder.okHttpClientBuilder().addInterceptor(chain -> chain.proceed(
      chain.request().newBuilder().header("x-api-key", apiKey).build()));
    return builder.build();
  }

  @Test
  void rejectedKeyWithExpiredClaim_isNotReplacedByTheTeamKey() throws Exception {
    Response<ConnectorsResponse> response =
      clientSending(EXPIRED_CLAIM_KEY).service().getConnectors().execute();

    assertEquals(401, response.code());
    assertTrue(api.received("/auth").isEmpty());
    assertEquals(Collections.singletonList(EXPIRED_CLAIM_KEY), api.apiKeysSentTo("/connectors"));
  }

  @Test
  void rejectedKeyThatIsNotAJwt_returnsTheRejection() throws Exception {
    Response<ConnectorsResponse> response =
      clientSending("not-a-jwt").service().getConnectors().execute();

    assertEquals(401, response.code());
    assertTrue(api.received("/auth").isEmpty());
  }

  @Test
  void acceptedKey_isSentUnchanged() throws Exception {
    acceptedKey = "caller-key";

    Response<ConnectorsResponse> response =
      clientSending("caller-key").service().getConnectors().execute();

    assertEquals(200, response.code());
    assertTrue(api.received("/auth").isEmpty());
    assertEquals(Collections.singletonList("caller-key"), api.apiKeysSentTo("/connectors"));
  }

  @Test
  void issuedKey_onceExpired_isStillRefreshedAndRetried() throws Exception {
    String expiredKey = FakePluggyServer.jwt("team", 1L);
    AtomicInteger authCalls = new AtomicInteger();
    api.on("POST", "/auth", request ->
      Reply.apiKey(authCalls.incrementAndGet() == 1 ? expiredKey : ISSUED_KEY));
    PluggyClient client = PluggyClient.builder()
      .baseUrl(api.url())
      .clientIdAndSecret("client-id", "client-secret")
      .build();

    Response<ConnectorsResponse> response = client.service().getConnectors().execute();

    assertEquals(200, response.code());
    assertEquals(2, authCalls.get());
    assertEquals(Arrays.asList(expiredKey, ISSUED_KEY), api.apiKeysSentTo("/connectors"));
  }
}
