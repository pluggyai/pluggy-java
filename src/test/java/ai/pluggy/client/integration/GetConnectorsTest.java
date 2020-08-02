package ai.pluggy.client.integration;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ai.pluggy.client.PluggyClient;
import ai.pluggy.client.request.ConnectorsSearchRequest;
import ai.pluggy.client.response.ConnectorsResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GetConnectorsTest {

  private static final String CLIENT_ID = "906a15c0-fdde-4dc5-9a23-df44455e1fb4";
  private static final String CLIENT_SECRET = "6fc93aec-9166-417c-8363-669167a39ce4";
  private PluggyClient client;

  @BeforeEach
  void setUp() {
    client = PluggyClient.builder().clientIdAndSecret(CLIENT_ID, CLIENT_SECRET).build();
  }

  @Test
  void getConnectors_withNoParams_returnsResults() throws IOException {
    ConnectorsResponse connectors = client.service().getConnectors().execute().body();

    assertNotNull(connectors);
    assertNotNull(connectors.getResults());
    assertTrue(connectors.getResults().size() > 0);
  }

  @Test
  void getConnectors_withParams_returnsFilteredResults() throws IOException {
    // request connectors with empty filters
    ConnectorsResponse allConnectors = client.service()
      .getConnectors(new ConnectorsSearchRequest()).execute().body();

    // request connectors with filters
    ConnectorsResponse connectorsFilteredByName = client.service()
      .getConnectors(new ConnectorsSearchRequest("Pluggy")).execute().body();
    ConnectorsResponse connectorsFilteredByOneCountry = client.service()
      .getConnectors(new ConnectorsSearchRequest(null, Collections.singletonList("AR"))).execute().body();
    ConnectorsResponse connectorsFilteredByTwoCountries = client.service()
      .getConnectors(new ConnectorsSearchRequest(null, Arrays.asList("BR", "AR"))).execute().body();
    ConnectorsResponse connectorsFilteredByOneCountryAndOneType = client.service()
      .getConnectors(new ConnectorsSearchRequest(null, Collections.singletonList("AR"),
        Collections.singletonList("BUSINESS_BANK"))).execute().body();

    int allCount = allConnectors.getResults().size();
    int byNameCount = connectorsFilteredByName.getResults().size();
    int byOneCountryCount = connectorsFilteredByOneCountry.getResults().size();
    int byTwoCountriesCount = connectorsFilteredByTwoCountries.getResults().size();
    int byOneCountryAndOneTypeCount = connectorsFilteredByOneCountryAndOneType.getResults().size();

    // assumes that API has data results for all filtered requests
    assertTrue(allCount > 0);
    assertTrue(byNameCount > 0);
    assertTrue(byOneCountryCount > 0);
    assertTrue(byTwoCountriesCount > 0);
    assertTrue(byOneCountryAndOneTypeCount > 0);

    // assumes filtered results are less than total result
    assertTrue(byNameCount < allCount);
    assertTrue(byOneCountryCount < allCount);
    assertTrue(byTwoCountriesCount <= allCount);
    assertTrue(byOneCountryAndOneTypeCount < allCount);

    // assumes filtering by one country results are less than by two countries
    assertTrue(byOneCountryCount < byTwoCountriesCount);
    assertTrue(byOneCountryAndOneTypeCount < byOneCountryCount);
  }
}
