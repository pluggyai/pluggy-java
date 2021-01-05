package ai.pluggy.client.request;

import ai.pluggy.client.response.ConnectorType;
import ai.pluggy.utils.Asserts;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

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

  public ConnectorsSearchRequest(String name, List<String> countries, List<ConnectorType> types) {
    if (name != null) {
      put("name", name);
    }
    if (countries != null && countries.size() > 0) {
      put("countries", String.join(",", countries));
    }
    if (types != null && types.size() > 0) {
      List<String> typesAsString = types.stream().map(Enum::toString).collect(Collectors.toList());
      put("types", String.join(",", typesAsString));
    }
  }

  public ConnectorsSearchRequest setName(String name) {
    Asserts.assertNotNull(name, "name");
    put("name", name);
    return this;
  }

  public ConnectorsSearchRequest setCountries(List<String> countries) {
    String field = "countries";
    Asserts.assertNotNull(countries, field);
    put(field, String.join(",", countries));
    return this;
  }

  public ConnectorsSearchRequest setTypes(List<ConnectorType> types) {
    String field = "types";
    Asserts.assertNotNull(types, field);
    List<String> typesAsString = types.stream().map(Enum::toString).collect(Collectors.toList());
    put(field, String.join(",", typesAsString));
    return this;
  }

  /**
   * @param includeSandbox If set to true, the response will include sandbox connectors. Otherwise, they won't be included.
   */
  public ConnectorsSearchRequest setIncludeSandbox(Boolean includeSandbox) {
    String field = "sandbox";
    Asserts.assertNotNull(includeSandbox, field);
    put(field, String.valueOf(includeSandbox));
    return this;
  }
}
