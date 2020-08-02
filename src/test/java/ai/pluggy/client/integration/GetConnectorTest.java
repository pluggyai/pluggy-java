package ai.pluggy.client.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import ai.pluggy.client.response.Connector;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import retrofit2.Response;

class GetConnectorTest extends BaseApiIntegrationTest {

  @Test
  void getConnector_byExistingId_returnsOneResult() throws IOException {
    Integer existingId = 1;
    Response<Connector> response = client.service().getConnector(existingId).execute();
    Connector existingConnector = response.body();
    assertNotNull(existingConnector);
    assertEquals(existingConnector.getId(), existingId);
  }

  @Test
  void getConnector_byNonExistingId_returnsNoResult() throws IOException {
    Integer nonExistingId = 99999;
    Response<Connector> response = client.service().getConnector(nonExistingId).execute();
    Connector nonExistingConnector = response.body();
    assertEquals(response.code(), 404);
    assertNull(nonExistingConnector);
  }
}
