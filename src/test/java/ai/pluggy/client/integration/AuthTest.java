package ai.pluggy.client.integration;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ai.pluggy.client.PluggyClient;
import ai.pluggy.exception.PluggyException;
import org.junit.jupiter.api.Test;

public class AuthTest {

  private static final String CLIENT_ID = "906a15c0-fdde-4dc5-9a23-df44455e1fb4";
  private static final String CLIENT_SECRET = "6fc93aec-9166-417c-8363-669167a39ce4";

  @Test
  public void validClientKeys_authenticate_shouldNotThrow() throws PluggyException {
    PluggyClient pluggy = PluggyClient.builder().clientIdAndSecret(CLIENT_ID, CLIENT_SECRET)
      .build();

    assertDoesNotThrow(pluggy::authenticate);
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
