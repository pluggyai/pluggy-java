package ai.pluggy.client.integration;

import static ai.pluggy.client.integration.util.AssertionsUtils.assertSuccessful;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ai.pluggy.client.response.ConnectTokenResponse;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;
import retrofit2.Call;
import retrofit2.Response;

public class CreateConnectTokenTest extends BaseApiIntegrationTest {

  @Test
  void createConnectToken_ok() throws IOException {

    Call<ConnectTokenResponse> createConnectTokenCall = client.service().createConnectToken();
    Response<ConnectTokenResponse> connectTokenResponse = createConnectTokenCall.execute();
    assertSuccessful(connectTokenResponse, client);
    ConnectTokenResponse connectTokenResponseBody = connectTokenResponse.body();
    assertNotNull(connectTokenResponseBody, "connectTokenResponse body should not be null");
    assertTrue(StringUtils.isNotBlank(connectTokenResponseBody.getAccessToken()),
      "connectTokenResponse.accessToken should not be null or empty");
  }
}
