package ai.pluggy.client.unit;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ai.pluggy.utils.Version;
import org.junit.jupiter.api.Test;

public class VersionTest {

  @Test
  void sdkVersion_isResolvedFromPom_notTheLiteralPlaceholder() {
    assertNotNull(Version.SDK_VERSION);
    assertTrue(Version.SDK_VERSION.matches("\\d+\\.\\d+\\.\\d+(-\\w+)?"),
        "SDK_VERSION should look like a semver, got: '" + Version.SDK_VERSION
            + "'. If it's '${project.version}', Maven resource filtering is broken.");
  }

  @Test
  void userAgent_followsExpectedFormat() {
    assertTrue(Version.USER_AGENT.startsWith("PluggyJava/" + Version.SDK_VERSION + " (Java "),
        "USER_AGENT should start with 'PluggyJava/<version> (Java ', got: '"
            + Version.USER_AGENT + "'");
    assertTrue(Version.USER_AGENT.endsWith(")"),
        "USER_AGENT should end with ')', got: '" + Version.USER_AGENT + "'");
  }
}
