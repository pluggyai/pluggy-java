package ai.pluggy.client.request;

import static ai.pluggy.utils.Asserts.assertNotEmpty;
import static ai.pluggy.utils.Asserts.assertNotNull;

import java.util.HashMap;
import java.util.List;

/**
 * Query params for GET /v2/transactions (cursor pagination). {@code accountId} is passed
 * separately to the service method. The API rejects unknown query params, so only the filters
 * below can be sent.
 */
public class TransactionsCursorSearchRequest extends HashMap<String, Object> {

  /**
   * @param ids List<String> - list of transaction ids to return
   * @return this instance, useful to continue adding params
   */
  public TransactionsCursorSearchRequest ids(List<String> ids) {
    assertNotEmpty(ids, "ids");
    put("ids", String.join(",", ids));
    return this;
  }

  /**
   * @param dateFrom String - transaction date lower bound, 'YYYY-MM-DD' or ISO-8601 date.
   * Cannot be combined with {@link #createdAtFrom(String)}.
   * @return this instance, useful to continue adding params
   */
  public TransactionsCursorSearchRequest dateFrom(String dateFrom) {
    assertNotNull(dateFrom, "dateFrom");
    assertNotCombined("createdAtFrom", "dateFrom");
    put("dateFrom", dateFrom);
    return this;
  }

  /**
   * @param dateTo String - transaction date upper bound, 'YYYY-MM-DD' or ISO-8601 date.
   * @return this instance, useful to continue adding params
   */
  public TransactionsCursorSearchRequest dateTo(String dateTo) {
    assertNotNull(dateTo, "dateTo");
    put("dateTo", dateTo);
    return this;
  }

  /**
   * @param createdAtFrom String - ISO-8601 timestamp; only transactions created in Pluggy from this
   * moment on. Cannot be combined with {@link #dateFrom(String)}.
   * @return this instance, useful to continue adding params
   */
  public TransactionsCursorSearchRequest createdAtFrom(String createdAtFrom) {
    assertNotNull(createdAtFrom, "createdAtFrom");
    assertNotCombined("dateFrom", "createdAtFrom");
    put("createdAtFrom", createdAtFrom);
    return this;
  }

  /**
   * @param after String - opaque cursor from a previous response
   * ({@code TransactionsCursorResponse#getNextCursor()}).
   * @return this instance, useful to continue adding params
   */
  public TransactionsCursorSearchRequest after(String after) {
    assertNotNull(after, "after");
    put("after", after);
    return this;
  }

  private void assertNotCombined(String existing, String adding) {
    if (containsKey(existing)) {
      throw new IllegalArgumentException(
        "'" + adding + "' cannot be combined with '" + existing + "'");
    }
  }

  public String getIds() {
    return (String) get("ids");
  }

  public String getDateFrom() {
    return (String) get("dateFrom");
  }

  public String getDateTo() {
    return (String) get("dateTo");
  }

  public String getCreatedAtFrom() {
    return (String) get("createdAtFrom");
  }

  public String getAfter() {
    return (String) get("after");
  }
}
