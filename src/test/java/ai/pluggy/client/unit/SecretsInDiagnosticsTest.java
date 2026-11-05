package ai.pluggy.client.unit;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ai.pluggy.client.PluggyClient;
import ai.pluggy.client.request.CreateItemRequest;
import ai.pluggy.client.request.ParametersMap;
import ai.pluggy.client.request.UpdateItemRequest;
import ai.pluggy.client.response.AuthResponse;
import ai.pluggy.client.response.ConnectTokenResponse;
import ai.pluggy.client.unit.FakePluggyServer.Reply;
import ai.pluggy.exception.PluggyException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Error messages, logs and toString output end up in places with wider access than the
 * credentials themselves, so keys, tokens and item credentials must stay out of them.
 */
public class SecretsInDiagnosticsTest {

  private FakePluggyServer api;
  private PluggyClient client;

  @BeforeEach
  void setUp() throws Exception {
    api = new FakePluggyServer();
    client = PluggyClient.builder()
      .baseUrl(api.url())
      .clientIdAndSecret("client-id", "client-secret")
      .build();
  }

  @AfterEach
  void tearDown() {
    api.close();
  }

  @Test
  void authenticate_unreadableSuccessfulResponse_keepsTheBodyOutOfTheError() {
    api.on("POST", "/auth", request -> Reply.json(200, "{\"apiKey\":{\"value\":\"leaked-key\"}}"));

    PluggyException error = assertThrows(PluggyException.class, client::authenticate);

    for (Throwable cause = error; cause != null; cause = cause.getCause()) {
      assertFalse(String.valueOf(cause.getMessage()).contains("leaked-key"), cause.toString());
    }
  }

  @Test
  void authenticate_rejection_keepsTheApiErrorInTheMessage() {
    api.on("POST", "/auth", request -> Reply.json(401, "{\"message\":\"invalid credentials\"}"));

    PluggyException error = assertThrows(PluggyException.class, client::authenticate);

    assertTrue(error.getMessage().contains("invalid credentials"), error.getMessage());
  }

  @Test
  void toString_omitsKeysTokensAndItemCredentials() {
    ParametersMap credentials = ParametersMap.map("password", "secret-password");

    assertFalse(new AuthResponse("secret-api-key").toString().contains("secret-api-key"));
    assertFalse(new ConnectTokenResponse("secret-token").toString().contains("secret-token"));
    assertFalse(new CreateItemRequest(0, credentials).toString().contains("secret-password"));
    assertFalse(UpdateItemRequest.builder().parameters(credentials).build().toString()
      .contains("secret-password"));
  }
}
