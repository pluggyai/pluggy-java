package ai.pluggy.client.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ai.pluggy.client.request.CreateItemRequest;
import ai.pluggy.client.request.ParametersMap;
import ai.pluggy.client.response.ItemResponse;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import retrofit2.Call;
import retrofit2.Response;

public class CreateItemTest extends BaseApiIntegrationTest {

  @SneakyThrows
  @Test
  void createItem_withValidParams_responseOk() {
    // create item params
    ParametersMap parametersMap = ParametersMap.map("user", "asd")
      .with("password", "qwe")
      .with("cuit", "20-34232323-2");
    Integer connectorId = 1;

    // run request with 'connectorId', 'parameters' params
    CreateItemRequest createItemRequest = new CreateItemRequest(connectorId, parametersMap);

    Call<ItemResponse> createItemRequestCall = client.service().createItem(createItemRequest);
    Response<ItemResponse> itemRequestResponse = createItemRequestCall.execute();
    assertTrue(itemRequestResponse.isSuccessful());
    ItemResponse itemResponse1 = itemRequestResponse.body();
    
    assertNotNull(itemResponse1);
    assertEquals(itemResponse1.getConnectorId(), connectorId);

    // run request with 'connectorId', 'parameters', 'webhookUrl' params
    String webhookUrl = "localhost:9999";
    CreateItemRequest createItemRequestWithWebhook = new CreateItemRequest(connectorId,
      parametersMap, webhookUrl);
    Response<ItemResponse> itemRequestWithWebhookResponse = client.service()
      .createItem(createItemRequestWithWebhook).execute();
    ItemResponse itemResponse2 = itemRequestWithWebhookResponse.body();
    
    assertNotNull(itemResponse2);
    assertEquals(itemResponse2.getConnectorId(), connectorId);
  }
}
