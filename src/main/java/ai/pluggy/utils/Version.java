package ai.pluggy.utils;

import java.io.InputStream;
import java.util.Properties;
import lombok.SneakyThrows;

public final class Version {

  public static final String SDK_VERSION = loadSdkVersion();
  public static final String USER_AGENT =
      "PluggyJava/" + SDK_VERSION + " (Java " + System.getProperty("java.version") + ")";

  private Version() {
  }

  @SneakyThrows
  private static String loadSdkVersion() {
    Properties props = new Properties();
    try (InputStream in = Version.class.getResourceAsStream("/pluggy-version.properties")) {
      props.load(in);
    }
    return props.getProperty("version");
  }
}
