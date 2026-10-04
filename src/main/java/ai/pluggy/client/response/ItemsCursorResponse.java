package ai.pluggy.client.response;

import ai.pluggy.utils.Cursors;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * GET /v2/items response: one cursor-paginated page of items, newest first.
 *
 * <p><b>Availability: opt-in, paid plans only.</b> Listing items is disabled by default and is
 * only available to paid-plan teams that have explicitly requested it from Pluggy support. Teams
 * without it enabled get {@code 403 LIST_ITEMS_FEATURE_NOT_ENABLED}. For most integrations, store
 * each itemId when it is created (Pluggy Connect {@code onSuccess} or the {@code item/created}
 * webhook) and use {@code getItem(id)} instead.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemsCursorResponse {

  List<ItemResponse> results;
  /**
   * Query string to fetch the next page (e.g. {@code ?clientUserId=...&after=<cursor>}), or null
   * on the last page. Use {@link #getNextCursor()} to extract the cursor.
   */
  String next;

  /**
   * @return the URL-decoded {@code after} cursor to request the next page with, or null when this
   * is the last page.
   */
  public String getNextCursor() {
    return Cursors.parseAfterCursor(next);
  }

  /**
   * @return true when there is another page to fetch
   */
  public boolean hasNext() {
    return getNextCursor() != null;
  }
}
