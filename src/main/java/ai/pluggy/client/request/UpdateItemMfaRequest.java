package ai.pluggy.client.request;

import java.util.HashMap;

public class UpdateItemMfaRequest extends HashMap<String, Object> {

  // HashMap param adder util
  public UpdateItemMfaRequest with(String key, Object value) {
    put(key, value);
    return this;
  }

  // HashMap builder util
  public static UpdateItemMfaRequest map(String key, Object value) {
    return new UpdateItemMfaRequest().with(key, value);
  }
}
