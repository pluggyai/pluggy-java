package ai.pluggy.client.request;

import static ai.pluggy.utils.Asserts.assertNotNull;

import ai.pluggy.client.response.ItemResourceStatus;
import java.util.HashMap;

public class ItemResourcesSearchRequest extends HashMap<String, Object> {

  /**
   * @param page Integer - page number to fetch, starting at page=1.
   * @return this instance, useful to continue adding params
   */
  public ItemResourcesSearchRequest page(Integer page) {
    assertNotNull(page, "page");
    put("page", page);
    return this;
  }

  /**
   * @param pageSize Integer - page size value, indicates max items to fetch per page.
   * @return this instance, useful to continue adding params
   */
  public ItemResourcesSearchRequest pageSize(Integer pageSize) {
    assertNotNull(pageSize, "pageSize");
    put("pageSize", pageSize);
    return this;
  }

  /**
   * @param status ItemResourceStatus - only resources the institution reports with this status.
   * @return this instance, useful to continue adding params
   */
  public ItemResourcesSearchRequest status(ItemResourceStatus status) {
    assertNotNull(status, "status");
    put("status", status.getValue());
    return this;
  }

  public Integer getPage() {
    if (!containsKey("page")) {
      return null;
    }
    return (Integer) get("page");
  }

  public Integer getPageSize() {
    if (!containsKey("pageSize")) {
      return null;
    }
    return (Integer) get("pageSize");
  }

  public String getStatus() {
    if (!containsKey("status")) {
      return null;
    }
    return (String) get("status");
  }
}
