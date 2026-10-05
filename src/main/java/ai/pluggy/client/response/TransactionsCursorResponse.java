package ai.pluggy.client.response;

import ai.pluggy.utils.Cursors;
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
    return Cursors.parseAfterCursor(next);
  }
}
