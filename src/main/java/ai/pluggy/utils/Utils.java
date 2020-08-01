package ai.pluggy.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

public class Utils {

  private static final String DEFAULT_ENCODING = "UTF-8";

  /**
   * Encode a string to URL http string
   *
   * @param string - value to be http encoded
   * @return the encoded string
   */
  public static String urlEncodeUTF8(String string) {
    try {
      return URLEncoder.encode(string, DEFAULT_ENCODING);
    } catch (UnsupportedEncodingException e) {
      // oh well
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }

  /**
   * Convert a params Map<String, String> object to a query string, ie. "?param1=val1&param2=val2" .
   * If map is null or empty, an empty string "" is returned.
   *
   * @param params - Map of params and values to format
   * @return - query string formatted
   */
  public static String formatQueryParams(Map<String, String> params) {
    if (params == null) {
      return "";
    }
    return params.entrySet().stream()
      .map(p -> urlEncodeUTF8(p.getKey()) + "=" + urlEncodeUTF8(p.getValue()))
      .reduce((p1, p2) -> p1 + "&" + p2)
      .map(s -> "?" + s)
      .orElse("");
  }

}
