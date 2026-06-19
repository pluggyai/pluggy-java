package ai.pluggy.utils;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class VersionTest {

  @Test
  public void version_isResolvedFromFilteredProperties() {
    // Resource filtering replaces ${project.version} at build time. If the filtering config
    // is removed, Version falls back to "unknown" and these assertions fail.
    String version = Version.get();
    assertFalse(version.isEmpty(), "version should not be empty");
    assertFalse("unknown".equals(version), "version should resolve from pluggy.properties");
    assertFalse(version.startsWith("${"), "version placeholder should be filtered");
  }

  @Test
  public void userAgent_hasExpectedPrefix() {
    assertTrue(Version.userAgent().startsWith("PluggyJava/"));
  }

  @Test
  public void userAgent_includesJavaVersionSuffix() {
    // e.g. "PluggyJava/1.11.0 (Java 1.8.0_292)" — gives the API visibility into the consumer's JVM.
    String userAgent = Version.userAgent();
    assertTrue(userAgent.startsWith("PluggyJava/" + Version.get() + " (Java "),
        "userAgent should start with 'PluggyJava/<version> (Java ', got: '" + userAgent + "'");
    assertTrue(userAgent.endsWith(")"), "userAgent should end with ')', got: '" + userAgent + "'");
  }
}
