package ai.pluggy.client.unit;

import static ai.pluggy.client.unit.FakePluggyServer.FAR_FUTURE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ai.pluggy.client.PluggyClient;
import ai.pluggy.client.PluggyClient.PluggyClientBuilder;
import ai.pluggy.client.request.CreateItemRequest;
import ai.pluggy.client.request.ParametersMap;
import ai.pluggy.client.unit.FakePluggyServer.Reply;
import com.google.gson.JsonParser;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import javax.crypto.Cipher;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * A builder can be kept and built again, so every client it produces must use only its own
 * credentials and interceptors.
 */
public class PluggyClientBuilderReuseTest {

  private static final String TEAM_A_KEY = FakePluggyServer.jwt("team-a", FAR_FUTURE);
  private static final String TEAM_B_KEY = FakePluggyServer.jwt("team-b", FAR_FUTURE);

  private FakePluggyServer api;

  @BeforeEach
  void setUp() throws Exception {
    api = new FakePluggyServer();
    // like the API, the key issued depends on the credentials presented
    api.on("POST", "/auth", request -> Reply.apiKey(FakePluggyServer.jwt(
      JsonParser.parseString(request.body).getAsJsonObject().get("clientId").getAsString(),
      FAR_FUTURE)));
    api.on("GET", "/connectors", request -> Reply.json(200, "{\"results\":[]}"));
    api.on("POST", "/items", request -> Reply.json(200, "{}"));
  }

  @AfterEach
  void tearDown() {
    api.close();
  }

  private PluggyClientBuilder teamABuilder() {
    return PluggyClient.builder().baseUrl(api.url()).clientIdAndSecret("team-a", "secret-a");
  }

  @Test
  void secondClient_sendsItsOwnKey() throws Exception {
    PluggyClientBuilder builder = teamABuilder();
    builder.build();
    PluggyClient second = builder.clientIdAndSecret("team-b", "secret-b").build();

    second.service().getConnectors().execute();

    assertEquals(Collections.singletonList(TEAM_B_KEY), api.apiKeysSentTo("/connectors"));
  }

  @Test
  void firstClient_keepsItsKey_afterSecondClientAuthenticates() throws Exception {
    PluggyClientBuilder builder = teamABuilder();
    PluggyClient first = builder.build();
    PluggyClient second = builder.clientIdAndSecret("team-b", "secret-b").build();

    second.authenticate();
    first.service().getConnectors().execute();

    assertEquals(Collections.singletonList(TEAM_A_KEY), api.apiKeysSentTo("/connectors"));
  }

  @Test
  void rebuildWithNoAuthInterceptor_dropsTheEarlierAuthentication() throws Exception {
    PluggyClientBuilder builder = teamABuilder();
    builder.build();
    PluggyClient second = builder.noAuthInterceptor().build();

    second.service().getConnectors().execute();

    assertEquals(Collections.singletonList(null), api.apiKeysSentTo("/connectors"));
    assertTrue(api.received("/auth").isEmpty());
  }

  @Test
  void rebuildWithRsaKey_encryptsParametersOnlyOnce() throws Exception {
    KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
    generator.initialize(2048);
    KeyPair keys = generator.generateKeyPair();
    String publicKey = "-----BEGIN PUBLIC KEY-----\n"
      + Base64.getEncoder().encodeToString(keys.getPublic().getEncoded())
      + "\n-----END PUBLIC KEY-----";
    PluggyClientBuilder builder = teamABuilder().noAuthInterceptor().rsaPublicKey(publicKey);
    builder.build();
    PluggyClient second = builder.build();

    second.service().createItem(new CreateItemRequest(0, ParametersMap.map("user", "u"))).execute();

    String encrypted = JsonParser.parseString(api.received("/items").get(0).body)
      .getAsJsonObject().get("parameters").getAsString();
    Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPPadding");
    cipher.init(Cipher.DECRYPT_MODE, keys.getPrivate());
    assertEquals("{\"user\":\"u\"}",
      new String(cipher.doFinal(Base64.getDecoder().decode(encrypted)), StandardCharsets.UTF_8));
  }

  @Test
  void callerInterceptors_applyToEveryClient() throws Exception {
    PluggyClientBuilder builder = teamABuilder();
    builder.okHttpClientBuilder().addInterceptor(chain -> chain.proceed(
      chain.request().newBuilder().addHeader("x-trace", "t").build()));
    PluggyClient first = builder.build();
    PluggyClient second = builder.build();

    first.service().getConnectors().execute();
    second.service().getConnectors().execute();

    assertEquals(2, api.received("/connectors").size());
    for (FakePluggyServer.Received request : api.received("/connectors")) {
      assertEquals(Arrays.asList("t"), request.headerValues("x-trace"));
    }
  }
}
