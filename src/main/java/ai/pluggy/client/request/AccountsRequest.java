package ai.pluggy.client.request;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * This request mapper class is used to make sure the accountTypes -> 'type' parameter mapping
 * is done correctly.
 */
public class AccountsRequest extends HashMap<String, String> {

  /**
   * Instantiate AccountsRequest with only 'itemId' param
   */
  public AccountsRequest(String itemId) {
    this(itemId, Collections.emptyList());
  }

  /**
   * Instantiate AccountsRequest with only 'itemId' and 'types' params
   */
  public AccountsRequest(String itemId, List<String> accountTypes) {
    if (itemId != null) {
      put("itemId", itemId);
    }
    if (accountTypes != null && accountTypes.size() > 0) {
      put("type", String.join(",", accountTypes));
    }
  }
}
