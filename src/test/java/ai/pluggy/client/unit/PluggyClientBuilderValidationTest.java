package ai.pluggy.client.unit;

import static org.junit.jupiter.api.Assertions.assertThrows;

import ai.pluggy.client.PluggyClient;
import org.junit.jupiter.api.Test;

public class PluggyClientBuilderValidationTest {

  @Test
  void rsaPublicKey_null_isRejected() {
    // a key read from an unset variable would otherwise silently disable encryption
    assertThrows(IllegalArgumentException.class, () -> PluggyClient.builder().rsaPublicKey(null));
  }
}
