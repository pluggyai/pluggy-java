package ai.pluggy.client.request;

import java.util.HashMap;

public class DateFilters extends HashMap<String, String> {

  /**
   * Define only 'from' date param.
   *
   * @param from date param, must be in 'YYYY-MM-DD' format
   */
  public DateFilters(String from) {
    this(from, null);
  }

  /**
   * Define 'from' and 'to' date params. If null, it's value is ignored.
   *
   * @param from - starting date - must be in 'YYYY-MM-DD' format. If null, it's ignored.
   * @param to   - end date - must be in 'YYYY-MM-DD' format
   */
  public DateFilters(String from, String to) {
    if (from != null) {
      put("from", from);
    }
    if (to != null) {
      put("to", to);
    }
  }
}
