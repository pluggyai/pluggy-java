package ai.pluggy.client.integration;

import static ai.pluggy.client.integration.helper.ItemHelper.createItem;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ai.pluggy.client.request.CreateItemRequest;
import ai.pluggy.client.request.ParametersMap;
import ai.pluggy.client.request.UpdateItemRequest;
import ai.pluggy.client.response.ErrorResponse;
import ai.pluggy.client.response.ItemResponse;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import retrofit2.Response;

public class UpdateItemTest extends BaseApiIntegrationTest {

  @SneakyThrows
  @Test
  void updateItem_beforeExecutionEnds_fails() {
    // precondition: an item already exists
    Integer connectorId = 1;
    ItemResponse itemResponse = createItem(client, connectorId);

    // build update item request
    ParametersMap newParameters = ParametersMap.map("user", "qwe");
    String newWebhookUrl = "localhost:3000";
    UpdateItemRequest updateItemRequest = UpdateItemRequest.builder()
      .webhookUrl(newWebhookUrl)
      .build();

    // run update item request
    Response<ItemResponse> updateItemResponse = client.service()
      .updateItem(itemResponse.getId(), updateItemRequest)
      .execute();
    ItemResponse updatedItem = updateItemResponse.body();

    ErrorResponse errorResponse = client.parseError(updateItemResponse);
    
    assertFalse(updateItemResponse.isSuccessful());
    assertNotNull(errorResponse);
    
    // expect response to include the updated data
    assertNotNull(updatedItem);
    assertEquals(updatedItem.getWebhookUrl(), newWebhookUrl);
  }

  @SneakyThrows
  @Test
  void updateItem_afterWaitingForCreation_ok() {
    // precondition: an item already exists
    Integer connectorId = 1;
    ItemResponse createdItemResponse = createItem(client, connectorId);

    // build update item request
    String newWebhookUrl = "localhost:3000";
    UpdateItemRequest updateItemRequest = UpdateItemRequest.builder()
      .webhookUrl(newWebhookUrl)
      .build();
    
    // wait for creation finish before updating, to prevent update request error 400
    Thread.sleep(15000);
    
    // run update item request
    Response<ItemResponse> updateItemResponse = client.service()
      .updateItem(createdItemResponse.getId(), updateItemRequest)
      .execute();
    
    ItemResponse updatedItem = updateItemResponse.body();

    // expect response to be successful
    assertTrue(updateItemResponse.isSuccessful());

    // expect item to be updated with the new data
    assertNotNull(updatedItem);
    assertNotEquals(createdItemResponse.getWebhookUrl(), newWebhookUrl);
    assertEquals(updatedItem.getWebhookUrl(), newWebhookUrl);
  }

}
