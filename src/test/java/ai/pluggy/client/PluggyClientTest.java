package ai.pluggy.client;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ai.pluggy.exception.PluggyException;
import org.junit.jupiter.api.Test;

public class PluggyClientTest {

  private static final String CLIENT_ID = "906a15c0-fdde-4dc5-9a23-df44455e1fb4";
  private static final String CLIENT_SECRET = "6fc93aec-9166-417c-8363-669167a39ce4";

  @Test
  public void validClientKeys_authenticateAPI_shouldNotThrow() throws PluggyException {
    PluggyClient pluggy = new PluggyClient();

    assertDoesNotThrow(() -> pluggy.authenticate(CLIENT_ID, CLIENT_SECRET));
  }

  @Test
  public void invalidClientKeys_authenticateAPI_shouldThrow() throws PluggyException {
    PluggyClient pluggy = new PluggyClient();

    assertThrows(PluggyException.class, () -> pluggy.authenticate("invalid-id", CLIENT_SECRET));
    assertThrows(PluggyException.class, () -> pluggy.authenticate(CLIENT_ID, "invalid-secret"));
  }


}
