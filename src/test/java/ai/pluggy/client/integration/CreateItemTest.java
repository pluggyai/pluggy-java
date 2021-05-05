package ai.pluggy.client.integration;

import static ai.pluggy.client.integration.util.AssertionsUtils.assertSuccessful;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
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
    ParametersMap parametersMap = ParametersMap.map("user", "user-ok")
      .with("password", "password-ok");
    Integer connectorId = 0;

    // run request with 'connectorId', 'parameters' params
    CreateItemRequest createItemRequest = new CreateItemRequest(connectorId, parametersMap);

    Call<ItemResponse> createItemRequestCall = client.service().createItem(createItemRequest);
    Response<ItemResponse> itemRequestResponse = createItemRequestCall.execute();
    assertSuccessful(itemRequestResponse, client);
    ItemResponse itemResponse1 = itemRequestResponse.body();

    assertNotNull(itemResponse1);
    assertEquals(itemResponse1.getConnector().getId(), connectorId);

    // run request with 'connectorId', 'parameters', 'webhookUrl', 'clientUserId', params
    String webhookUrl = "https://www.test.com/";
    String clientUserId = "clientUserId";
    CreateItemRequest createItemRequestWithWebhook = new CreateItemRequest(connectorId,
      parametersMap, webhookUrl, clientUserId);
    Response<ItemResponse> itemRequestWithWebhookResponse = client.service()
      .createItem(createItemRequestWithWebhook).execute();
    ItemResponse itemResponse2 = itemRequestWithWebhookResponse.body();

    assertNotNull(itemResponse2);
    assertEquals(itemResponse2.getConnector().getId(), connectorId);
    assertEquals(itemResponse2.getClientUserId(), clientUserId);
    assertEquals(itemResponse2.getWebhookUrl(), webhookUrl);
    
    this.getItemsIdCreated().add(itemResponse1.getId());
    this.getItemsIdCreated().add(itemResponse2.getId());
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
    assertNotNull(errorResponse.getDetails(), "should include validation error details for bad params");
    assertTrue(errorResponse.getDetails().size() > 0);

    // webhookUrl param 'localhost' is invalid, expect error 400
    String localWebhookUrl = "localhost:9999";
    CreateItemRequest createItemRequestWithLocalWebhook = new CreateItemRequest(connectorId,
      parametersMap, localWebhookUrl);
    Response<ItemResponse> itemRequestWithLocalWebhookResponse = client.service()
      .createItem(createItemRequestWithLocalWebhook).execute();
    ErrorResponse localWebhookErrorResponse = client.parseError(itemRequestWithLocalWebhookResponse);
    assertNotNull(localWebhookErrorResponse);
    assertEquals(localWebhookErrorResponse.getCode(), 400);
    assertNull(localWebhookErrorResponse.getDetails(), "should not include validation error details for webhookUrl");

    // webhookUrl param http is invalid, expect error 400
    String httpWebhookUrl = "http://www.test.com";
    CreateItemRequest createItemRequestWithHttpWebhook = new CreateItemRequest(connectorId,
      parametersMap, httpWebhookUrl);
    Response<ItemResponse> itemRequestWithHttpWebhookResponse = client.service()
      .createItem(createItemRequestWithHttpWebhook).execute();
    ErrorResponse httpWebhookErrorResponse = client.parseError(itemRequestWithHttpWebhookResponse);
    assertNotNull(httpWebhookErrorResponse);
    assertEquals(httpWebhookErrorResponse.getCode(), 400);
    assertNull(localWebhookErrorResponse.getDetails(), "should not include validation error details for webhookUrl");
  }
}
