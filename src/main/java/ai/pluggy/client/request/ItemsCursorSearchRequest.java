package ai.pluggy.client.request;

import static ai.pluggy.utils.Asserts.assertNotNull;

import java.util.HashMap;

/**
 * Query params for GET /v2/items (cursor pagination). All filters are optional. The API rejects
 * unknown query params, so only the filters below can be sent.
 *
 * <p><b>Availability: opt-in, paid plans only.</b> Listing items is disabled by default and is
 * only available to paid-plan teams that have explicitly requested it from Pluggy support. Teams
 * without it enabled get {@code 403 LIST_ITEMS_FEATURE_NOT_ENABLED}. For most integrations, store
 * each itemId when it is created (Pluggy Connect {@code onSuccess} or the {@code item/created}
 * webhook) and use {@code getItem(id)} instead.
 */
public class ItemsCursorSearchRequest extends HashMap<String, Object> {

  private static final int CLIENT_USER_ID_MAX_LENGTH = 255;

  /**
   * @param clientUserId String - only items created with this clientUserId (max 255 chars)
   * @return this instance, useful to continue adding params
   */
  public ItemsCursorSearchRequest clientUserId(String clientUserId) {
    assertNotNull(clientUserId, "clientUserId");
    if (clientUserId.length() > CLIENT_USER_ID_MAX_LENGTH) {
      throw new IllegalArgumentException(
        "'clientUserId' cannot be longer than " + CLIENT_USER_ID_MAX_LENGTH + " characters");
    }
    put("clientUserId", clientUserId);
    return this;
  }

  /**
   * @param connectorId Integer - only items of this connector (>= 0)
   * @return this instance, useful to continue adding params
   */
  public ItemsCursorSearchRequest connectorId(Integer connectorId) {
    assertNotNull(connectorId, "connectorId");
    if (connectorId < 0) {
      throw new IllegalArgumentException("'connectorId' cannot be negative");
    }
    put("connectorId", connectorId);
    return this;
  }

  /**
   * @param after String - opaque cursor from a previous response
   * ({@code ItemsCursorResponse#getNextCursor()}).
   * @return this instance, useful to continue adding params
   */
  public ItemsCursorSearchRequest after(String after) {
    assertNotNull(after, "after");
    put("after", after);
    return this;
  }

  public String getClientUserId() {
    return (String) get("clientUserId");
  }

  public Integer getConnectorId() {
    return (Integer) get("connectorId");
  }

  public String getAfter() {
    return (String) get("after");
  }
}
