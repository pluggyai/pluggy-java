package ai.pluggy.client.request;

import static ai.pluggy.utils.Asserts.assertNotEmpty;
import static ai.pluggy.utils.Asserts.assertNotNull;
import static ai.pluggy.utils.Asserts.assertValidDateString;

import java.util.HashMap;
import java.util.List;

public class TransactionsSearchRequest extends HashMap<String, Object> {

  public static final String DATE_PARAM_FORMAT_ISO = "yyyy-MM-dd";

  /**
   * @param ids List<String> - list of ids to return
   * @return this instance, useful to continue adding params
   */
  public TransactionsSearchRequest ids(List<String> ids) {
    assertNotEmpty(ids, "ids");
    put("ids", String.join(",", ids));
    return this;
  }

  /**
   * @param fromDate String - from date in 'YYYY-MM-DD' format
   * @return this instance, useful to continue adding params
   */
  public TransactionsSearchRequest from(String fromDate) {
    validateDateString(fromDate, "from");
    put("from", fromDate);
    return this;
  }

  /**
   * @param fromDate String - from date in 'YYYY-MM-DD' format
   * @return this instance, useful to continue adding params
   */
  public TransactionsSearchRequest to(String fromDate) {
    validateDateString(fromDate, "to");
    put("to", fromDate);
    return this;
  }

  /**
   * @param page Integer - page number to fetch, starting at page=1.
   * @return this instance, useful to continue adding params
   */
  public TransactionsSearchRequest page(Integer page) {
    assertNotNull(page, "page");
    put("page", page);
    return this;
  }

  /**
   * @param pageSize Integer - page size value, indicates max items to fetch per page.
   * @return this instance, useful to continue adding params
   */
  public TransactionsSearchRequest pageSize(Integer pageSize) {
    assertNotNull(pageSize, "pageSize");
    put("pageSize", pageSize);
    return this;
  }


  private void validateDateString(String dateString, String name) {
    assertValidDateString(dateString, DATE_PARAM_FORMAT_ISO, name);
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

  public String getFrom() {
    if (!containsKey("from")) {
      return null;
    }
    return (String) get("from");
  }
  
  public String getTo() {
    if (!containsKey("to")) {
      return null;
    }
    return (String) get("to");
  }

  public List<String> getIds() {
    if (!containsKey("ids")) {
      return null;
    }
    String ids = (String) get("ids");
    return List.of(ids.split(","));
  }
}
