package ai.pluggy.client.integration;

import static ai.pluggy.client.integration.helper.ItemHelper.createPluggyBankItem;
import static ai.pluggy.client.integration.helper.ItemHelper.getItemStatus;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ai.pluggy.client.integration.util.Poller;
import ai.pluggy.client.request.UpdateItemRequest;
import ai.pluggy.client.response.ErrorResponse;
import ai.pluggy.client.response.ItemResponse;
import ai.pluggy.client.response.ItemStatus;

import java.util.Objects;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import retrofit2.Response;

public class UpdateItemTest extends BaseApiIntegrationTest {

  @SneakyThrows
  @Test
  void updateItem_beforeExecutionEnds_errorResponse() {
    // precondition: an item already exists
    ItemResponse itemResponse = createPluggyBankItem(client);

    // build update item request
    String newWebhookUrl = "https://www.test.com";
    String newClientUserId = "clientUserId";
    UpdateItemRequest updateItemRequest = UpdateItemRequest.builder()
        .webhookUrl(newWebhookUrl)
        .clientUserId(newClientUserId)
        .build();

    // run update item request
    Response<ItemResponse> updateItemResponse = client.service()
        .updateItem(itemResponse.getId(), updateItemRequest)
        .execute();

    // expect update item response to be an error
    ErrorResponse errorResponse = client.parseError(updateItemResponse);

    assertFalse(updateItemResponse.isSuccessful());
    assertNotNull(errorResponse);
    assertEquals(errorResponse.getCode(), 400);

    // expect updatedItem response to be null
    ItemResponse updatedItem = updateItemResponse.body();
    assertNull(updatedItem);

    this.getItemsIdCreated().add(itemResponse.getId());
  }

  @SneakyThrows
  @Test
  void updateItem_afterWaitingForCreation_ok() {
    // precondition: an item already exists
    ItemResponse createdItemResponse = createPluggyBankItem(client);

    // build update item request
    String newWebhookUrl = "https://www.test2.com";
    String newClientUserId = "clientUserId";
    UpdateItemRequest updateItemRequest = UpdateItemRequest.builder()
        .webhookUrl(newWebhookUrl)
        .clientUserId(newClientUserId)
        .build();

    // wait for creation finish (status: "UPDATED") before updating, to prevent
    // update request error 400.
    Poller.pollRequestUntil(
        () -> getItemStatus(client, createdItemResponse.getId()),
        (ItemResponse itemStatusResponse) -> Objects
            .equals(itemStatusResponse.getStatus(), ItemStatus.UPDATED),
        500, 45000);

    // run update item request
    Response<ItemResponse> updateItemResponse = client.service()
        .updateItem(createdItemResponse.getId(), updateItemRequest)
        .execute();

    ItemResponse updatedItem = updateItemResponse.body();

    // expect response to be successful
    assertTrue(updateItemResponse.isSuccessful());

    // expect item to be updated with the new data
    assertNotNull(updatedItem);
    assertEquals(updatedItem.getWebhookUrl(), newWebhookUrl);
    assertNotEquals(createdItemResponse.getWebhookUrl(), newWebhookUrl);

    this.getItemsIdCreated().add(createdItemResponse.getId());
  }

  @SneakyThrows
  @Test
  void updateItem_afterCreated_withEmptyData_ok() {
    // precondition: an item already exists
    ItemResponse createdItemResponse = createPluggyBankItem(client);

    // build empty update item request
    UpdateItemRequest emptyUpdateItemRequest = UpdateItemRequest.builder()
        .build();

    // wait for creation finish (status: "UPDATED") before updating, to prevent
    // update request error 400.
    Poller.pollRequestUntil(
        () -> getItemStatus(client, createdItemResponse.getId()),
        (ItemResponse itemStatusResponse) -> Objects
            .equals(itemStatusResponse.getStatus(), ItemStatus.UPDATED),
        500, 45000);

    // run update item request with empty params
    Response<ItemResponse> updateItemResponseEmptyParams = client.service()
        .updateItem(createdItemResponse.getId(), emptyUpdateItemRequest)
        .execute();

    // expect response (to empty params request) to be successful
    assertTrue(updateItemResponseEmptyParams.isSuccessful());
    ItemResponse updatedItem = updateItemResponseEmptyParams.body();

    // expect item to be status = "UPDATING"
    assertNotNull(updatedItem);
    ItemResponse updatingItemStatus = getItemStatus(client, createdItemResponse.getId());
    assertEquals(updatingItemStatus.getStatus(), ItemStatus.UPDATING);

    this.getItemsIdCreated().add(createdItemResponse.getId());
  }

  @SneakyThrows
  @Test
  void updateItem_afterCreated_withNoData_ok() {
    // precondition: an item already exists
    ItemResponse createdItemResponse = createPluggyBankItem(client);

    // wait for creation finish (status: "UPDATED") before updating, to prevent
    // update request error 400.
    Poller.pollRequestUntil(
        () -> getItemStatus(client, createdItemResponse.getId()),
        (ItemResponse itemStatusResponse) -> Objects
            .equals(itemStatusResponse.getStatus(), ItemStatus.UPDATED),
        500, 45000);

    // run update item request with empty params
    Response<ItemResponse> updateItemResponseNullParams = client.service()
        .updateItem(createdItemResponse.getId())
        .execute();

    // expect response (to null-params request) to be successful
    assertTrue(updateItemResponseNullParams.isSuccessful());
    ItemResponse updatedItem = updateItemResponseNullParams.body();

    // expect item to be status = "UPDATING"
    assertNotNull(updatedItem);
    ItemResponse updatingItemStatus = getItemStatus(client, createdItemResponse.getId());
    assertEquals(updatingItemStatus.getStatus(), ItemStatus.UPDATING);

    this.getItemsIdCreated().add(createdItemResponse.getId());
  }
}
