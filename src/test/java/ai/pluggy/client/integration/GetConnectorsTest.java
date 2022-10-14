package ai.pluggy.client.integration;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ai.pluggy.client.request.ConnectorsSearchRequest;
import ai.pluggy.client.response.ConnectorType;
import ai.pluggy.client.response.ConnectorsResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.Test;

class GetConnectorsTest extends BaseApiIntegrationTest {

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
    ConnectorsResponse defaultConnectors = client.service()
      .getConnectors(new ConnectorsSearchRequest()).execute().body();

    // request connectors with filters
    ConnectorsResponse connectorsFilteredByName = client.service()
      .getConnectors(new ConnectorsSearchRequest("C6 Bank")).execute().body();
    ConnectorsResponse connectorsFilteredIncludeSandbox = client.service()
      .getConnectors(new ConnectorsSearchRequest().setIncludeSandbox(true)).execute().body();
    ConnectorsResponse connectorsFilteredByOneCountryAndOneType = client.service()
      .getConnectors(new ConnectorsSearchRequest(null, Collections.singletonList("BR"),
        Collections.singletonList(ConnectorType.BUSINESS_BANK))).execute().body();

    int allCount = defaultConnectors.getResults().size();
    int allIncludeSandboxCount = connectorsFilteredIncludeSandbox.getResults().size();
    int byNameCount = connectorsFilteredByName.getResults().size();
    int byOneCountryAndOneTypeCount = connectorsFilteredByOneCountryAndOneType.getResults().size();

    // assumes that API has data results for all filtered requests
    assertTrue(allCount > 0);
    assertTrue(allIncludeSandboxCount > 0);
    assertTrue(byNameCount > 0);
    assertTrue(byOneCountryAndOneTypeCount > 0);

    // assumes filtered results are less than default total result
    assertTrue(byNameCount < allCount);
    assertTrue(byOneCountryAndOneTypeCount < allCount);

    // assumes including sandbox results are more (or equal) than default total results
    assertTrue(allIncludeSandboxCount >= allCount);
  }
}
