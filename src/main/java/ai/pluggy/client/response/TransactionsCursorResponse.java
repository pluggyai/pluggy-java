package ai.pluggy.client.response;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * GET /v2/transactions response: one cursor-paginated page of transactions.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionsCursorResponse {

  List<Transaction> results;
  /**
   * Query string to fetch the next page (e.g. {@code ?accountId=...&after=<cursor>}), or null on
   * the last page. Use {@link #getNextCursor()} to extract the cursor.
   */
  String next;

  /**
   * @return the URL-decoded {@code after} cursor to request the next page with, or null when this
   * is the last page.
   */
  public String getNextCursor() {
    return parseAfterCursor(next);
  }

  /**
   * @return true when there is another page to fetch
   */
  public boolean hasNext() {
    return getNextCursor() != null;
  }

  /**
   * Extracts the URL-decoded {@code after} parameter from a {@code next} query string.
   *
   * @param next String - the {@code next} value of a /v2/transactions response
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
