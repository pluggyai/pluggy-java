package ai.pluggy.client.request;

import java.util.HashMap;

public class ParametersMap extends HashMap<String, Object> {

  // HashMap param adder util
  public ParametersMap with(String key, Object value) {
    put(key, value);
    return this;
  }

  // HashMap builder util
  public static ParametersMap map(String key, Object value) {
    return new ParametersMap().with(key, value);
  }
}
