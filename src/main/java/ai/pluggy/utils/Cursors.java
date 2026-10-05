package ai.pluggy.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * Helpers for the cursor-paginated endpoints ({@code GET /v2/transactions},
 * {@code GET /v2/items}), whose responses carry a {@code next} query string.
 */
public final class Cursors {

  private Cursors() {
  }

  /**
   * Extracts the URL-decoded {@code after} parameter from a {@code next} query string.
   *
   * @param next String - the {@code next} value of a cursor-paginated response
   * @return the cursor, or null if {@code next} is null or has no {@code after} parameter
   */
  public static String parseAfterCursor(String next) {
    if (next == null || next.isEmpty()) {
      return null;
    }
    String query = next;
    int questionMark = query.indexOf('?');
    if (questionMark >= 0) {
      query = query.substring(questionMark + 1);
    }
    int fragment = query.indexOf('#');
    if (fragment >= 0) {
      query = query.substring(0, fragment);
    }
    for (String pair : query.split("&")) {
      int eq = pair.indexOf('=');
      String key = eq >= 0 ? pair.substring(0, eq) : pair;
      if ("after".equals(key)) {
        String value = eq >= 0 ? pair.substring(eq + 1) : "";
        if (value.isEmpty()) {
          return null;
        }
        try {
          return URLDecoder.decode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
          throw new IllegalStateException(e);
        }
      }
    }
    return null;
  }
}
