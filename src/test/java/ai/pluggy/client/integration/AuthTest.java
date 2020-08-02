package ai.pluggy.client.integration;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ai.pluggy.client.PluggyClient;
import ai.pluggy.exception.PluggyException;
import org.junit.jupiter.api.Test;

public class AuthTest extends BaseApiIntegrationTest {

  @Test
  public void validClientKeys_authenticate_shouldNotThrow() throws PluggyException {
    assertDoesNotThrow(client::authenticate);
  }

  @Test
  public void invalidClientKeys_authenticate_shouldThrow() throws PluggyException {
    PluggyClient pluggyClientInvalidClientId = PluggyClient.builder()
      .clientIdAndSecret("invalid-id", CLIENT_SECRET)
      .build();
    PluggyClient pluggyClientInvalidClientSecret = PluggyClient.builder()
      .clientIdAndSecret(CLIENT_ID, "invalid-secret")
      .build();

    assertThrows(PluggyException.class, pluggyClientInvalidClientId::authenticate);
    assertThrows(PluggyException.class, pluggyClientInvalidClientSecret::authenticate);
  }
}
