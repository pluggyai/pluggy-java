package ai.pluggy.client.request;

import static ai.pluggy.utils.Asserts.assertNotNull;
import static ai.pluggy.utils.Asserts.assertValidDateString;

import java.util.HashMap;

public class SmartTransferPreauthorizationPaymentsSearchRequest extends HashMap<String, Object> {

  public static final String DATE_PARAM_FORMAT_ISO = "yyyy-MM-dd";

  /**
   * @param fromDate String - only payments created from this date, in 'YYYY-MM-DD' format
   * @return this instance, useful to continue adding params
   */
  public SmartTransferPreauthorizationPaymentsSearchRequest from(String fromDate) {
    assertValidDateString(fromDate, DATE_PARAM_FORMAT_ISO, "from");
    put("from", fromDate);
    return this;
  }

  /**
   * @param toDate String - only payments created until this date, in 'YYYY-MM-DD' format
   * @return this instance, useful to continue adding params
   */
  public SmartTransferPreauthorizationPaymentsSearchRequest to(String toDate) {
    assertValidDateString(toDate, DATE_PARAM_FORMAT_ISO, "to");
    put("to", toDate);
    return this;
  }

  /**
   * @param page Integer - page number to fetch, starting at page=1.
   * @return this instance, useful to continue adding params
   */
  public SmartTransferPreauthorizationPaymentsSearchRequest page(Integer page) {
    assertNotNull(page, "page");
    put("page", page);
    return this;
  }

  /**
   * @param pageSize Integer - page size value, indicates max items to fetch per page.
   * @return this instance, useful to continue adding params
   */
  public SmartTransferPreauthorizationPaymentsSearchRequest pageSize(Integer pageSize) {
    assertNotNull(pageSize, "pageSize");
    put("pageSize", pageSize);
    return this;
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
}
