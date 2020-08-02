package ai.pluggy.client.request;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class ConnectorsSearchRequest extends HashMap<String, String> {

  public ConnectorsSearchRequest() {
    this(null, Collections.emptyList(), Collections.emptyList());
  }

  public ConnectorsSearchRequest(String name) {
    this(name, Collections.emptyList(), Collections.emptyList());
  }

  public ConnectorsSearchRequest(String name, List<String> countries) {
    this(name, countries, Collections.emptyList());
  }

  public ConnectorsSearchRequest(String name, List<String> countries, List<String> types) {
    if (name != null) {
      put("name", name);
    }
    if (countries != null && countries.size() > 0) {
      put("countries", String.join(",", countries));
    }
    if (types != null && types.size() > 0) {
      put("types", String.join(",", types));
    }
  }

}
