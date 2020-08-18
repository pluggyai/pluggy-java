package ai.pluggy.client.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ai.pluggy.client.request.CreateItemRequest;
import ai.pluggy.client.request.ParametersMap;
import ai.pluggy.client.response.ErrorResponse;
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
    Integer connectorId = 101;

    // run request with 'connectorId', 'parameters' params
    CreateItemRequest createItemRequest = new CreateItemRequest(connectorId, parametersMap);

    Call<ItemResponse> createItemRequestCall = client.service().createItem(createItemRequest);
    Response<ItemResponse> itemRequestResponse = createItemRequestCall.execute();
    assertTrue(itemRequestResponse.isSuccessful());
    ItemResponse itemResponse1 = itemRequestResponse.body();

    assertNotNull(itemResponse1);
    assertEquals(itemResponse1.getConnectorId(), connectorId);

    // run request with 'connectorId', 'parameters', 'webhookUrl' params
    String webhookUrl = "https://www.test.com/";
    CreateItemRequest createItemRequestWithWebhook = new CreateItemRequest(connectorId,
      parametersMap, webhookUrl);
    Response<ItemResponse> itemRequestWithWebhookResponse = client.service()
      .createItem(createItemRequestWithWebhook).execute();
    ItemResponse itemResponse2 = itemRequestWithWebhookResponse.body();

    assertNotNull(itemResponse2);
    assertEquals(itemResponse2.getConnectorId(), connectorId);
  }

  @SneakyThrows
  @Test
  void createItem_withInvalidParams_responseError400() {
    // create item params
    ParametersMap parametersMap = ParametersMap
      .map("bad-param-key", "asd")
      .with("other-bad-param-key", "qwe");
    Integer connectorId = 0;

    // run request with 'connectorId', 'parameters' params
    CreateItemRequest createItemRequest = new CreateItemRequest(connectorId, parametersMap);

    Call<ItemResponse> createItemRequestCall = client.service().createItem(createItemRequest);
    Response<ItemResponse> itemRequestResponse = createItemRequestCall.execute();

    assertFalse(itemRequestResponse.isSuccessful());

    ErrorResponse errorResponse = client.parseError(itemRequestResponse);
    assertNotNull(errorResponse);
    assertEquals(errorResponse.getCode(), 400);

    // webhookUrl param 'localhost' is invalid, expect error 400
    String localWebhookUrl = "localhost:9999";
    CreateItemRequest createItemRequestWithLocalWebhook = new CreateItemRequest(connectorId,
      parametersMap, localWebhookUrl);
    Response<ItemResponse> itemRequestWithLocalWebhookResponse = client.service()
      .createItem(createItemRequestWithLocalWebhook).execute();
    ErrorResponse localWebhookErrorResponse = client.parseError(itemRequestWithLocalWebhookResponse);
    assertNotNull(localWebhookErrorResponse);
    assertEquals(localWebhookErrorResponse.getCode(), 400);

    // webhookUrl param http is invalid, expect error 400
    String httpWebhookUrl = "http://www.test.com";
    CreateItemRequest createItemRequestWithHttpWebhook = new CreateItemRequest(connectorId,
      parametersMap, httpWebhookUrl);
    Response<ItemResponse> itemRequestWithHttpWebhookResponse = client.service()
      .createItem(createItemRequestWithHttpWebhook).execute();
    ErrorResponse httpWebhookErrorResponse = client.parseError(itemRequestWithHttpWebhookResponse);
    assertNotNull(httpWebhookErrorResponse);
    assertEquals(httpWebhookErrorResponse.getCode(), 400);
  }
}
