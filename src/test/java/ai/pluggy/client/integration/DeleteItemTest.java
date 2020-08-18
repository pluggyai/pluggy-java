package ai.pluggy.client.integration;

import static ai.pluggy.client.integration.helper.ItemHelper.NON_EXISTING_ITEM_ID;
import static ai.pluggy.client.integration.helper.ItemHelper.createItem;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import ai.pluggy.client.response.DeleteItemResponse;
import ai.pluggy.client.response.ErrorResponse;
import ai.pluggy.client.response.ItemResponse;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import retrofit2.Response;

public class DeleteItemTest extends BaseApiIntegrationTest {

  @Test
  void deleteItem_existingItemId_ok() throws IOException {
    // ensure item exists
    Integer connectorId = 101;
    ItemResponse item = createItem(client, connectorId);
    assertNotNull(item);

    String existingItemId = item.getId();

    // delete item by id
    Response<DeleteItemResponse> deleteItemResponse = client.service()
      .deleteItem(existingItemId)
      .execute();

    // expect response to be OK
    assertEquals(deleteItemResponse.code(), 200);

    DeleteItemResponse itemResponse = deleteItemResponse.body();
    assertNotNull(itemResponse);
  }

  @Test
  void deleteItem_nonExistingItemId_error404() throws IOException {
    // delete item by non-existing id
    Response<DeleteItemResponse> deleteItemResponse = client.service()
      .deleteItem(NON_EXISTING_ITEM_ID)
      .execute();

    // expect response to not be OK
    assertFalse(deleteItemResponse.isSuccessful());

    ErrorResponse errorResponse = client.parseError(deleteItemResponse);
    assertNotNull(errorResponse);
    assertEquals(errorResponse.getCode(), 404);
  }
}
