package ai.pluggy.client.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import ai.pluggy.client.response.Connector;
import ai.pluggy.client.response.CredentialLabel;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import retrofit2.Response;

class GetConnectorTest extends BaseApiIntegrationTest {

  @Test
  void getConnector_byExistingId_returnsOneResult() throws IOException {
    Integer existingConnectorId = 0;
    Response<Connector> response = client.service().getConnector(existingConnectorId).execute();
    Connector existingConnector = response.body();
    assertNotNull(existingConnector);
    assertEquals(existingConnector.getId(), existingConnectorId);
  }

  @Test
  void getConnector_byNonExistingId_returnsNoResult() throws IOException {
    Integer nonExistingConnectorId = 99999;
    Response<Connector> response = client.service().getConnector(nonExistingConnectorId).execute();
    Connector nonExistingConnector = response.body();
    assertEquals(response.code(), 404);
    assertNull(nonExistingConnector);
  }

  @Test
  void getConnector_bySandboxId_responseHasMfaFields() throws IOException {
    Integer pluggyConnectorId = 1;
    Response<Connector> response = client.service().getConnector(pluggyConnectorId).execute();
    Connector existingConnector = response.body();
    assertNotNull(existingConnector);
    assertEquals(existingConnector.getId(), pluggyConnectorId);
    assertEquals(existingConnector.getHasMFA(), true);

    Optional<CredentialLabel> mfaCredential = existingConnector.getCredentials().stream()
      .filter(credentialLabel -> Objects.equals(credentialLabel.getMfa(), true)).findFirst();
    assertEquals(mfaCredential.isPresent(), true);
    assertEquals(mfaCredential.isPresent() && mfaCredential.get().getMfa(), true);
  }
}
