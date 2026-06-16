package ai.pluggy.utils;

import java.io.InputStream;
import java.util.Properties;

/**
 * Exposes the SDK version, derived from the Maven {@code project.version} at build time via
 * resource filtering of {@code pluggy.properties}. Used to build the {@code User-Agent} header so it
 * stays in sync with {@code pom.xml} automatically.
 */
public final class Version {

  private static final String UNKNOWN = "unknown";
  private static final String VERSION = load();

  private Version() {
  }

  /** SDK version (e.g. {@code "1.11.0"}), or {@code "unknown"} if it can't be resolved. */
  public static String get() {
    return VERSION;
  }

  /** {@code User-Agent} header value, e.g. {@code "PluggyJava/1.11.0"}. */
  public static String userAgent() {
    return "PluggyJava/" + VERSION;
  }

  private static String load() {
    try (InputStream in = Version.class.getResourceAsStream("/pluggy.properties")) {
      if (in == null) {
        return UNKNOWN;
      }
      Properties props = new Properties();
      props.load(in);
      String version = props.getProperty("version");
      // Guard against an unfiltered placeholder (e.g. if resource filtering didn't run).
      if (version == null || version.isEmpty() || version.startsWith("${")) {
        return UNKNOWN;
      }
      return version;
    } catch (Exception e) {
      return UNKNOWN;
    }
  }
}
