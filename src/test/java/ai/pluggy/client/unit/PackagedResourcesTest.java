package ai.pluggy.client.unit;

import static org.junit.jupiter.api.Assertions.assertNull;

import ai.pluggy.client.PluggyClient;
import org.junit.jupiter.api.Test;

public class PackagedResourcesTest {

  @Test
  void sdkShipsNoLog4jConfiguration() {
    // Log4j looks for log4j2.properties before an application's log4j2.xml, so a file in the
    // SDK jar, even an empty one, would replace the application's logging configuration
    assertNull(PluggyClient.class.getResource("/log4j2.properties"));
  }
}
