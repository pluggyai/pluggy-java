package ai.pluggy.client.integration;

import static ai.pluggy.client.integration.helper.ItemHelper.NON_EXISTING_ITEM_ID;
import static ai.pluggy.client.integration.helper.ItemHelper.createItem;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import ai.pluggy.client.response.ErrorResponse;
import ai.pluggy.client.response.ItemResponse;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import retrofit2.Response;

public class GetItemTest extends BaseApiIntegrationTest {

  @Test
  void getItem_existingItem_ok() throws IOException {
    // ensure item exists
    Integer connectorId = 1;
    ItemResponse item = createItem(client, connectorId);
    assertNotNull(item);

    String createdItemId = item.getId();

    // get item by id
    ItemResponse itemResponse = client.service().getItem(createdItemId).execute().body();

    // expect item response id to match requested
    assertNotNull(itemResponse);
    assertEquals(itemResponse.getId(), createdItemId);
  }

  @Test
  void getItem_nonExistingItem_errorResponse404() throws IOException {
    Response<ItemResponse> getItemResponse = client.service().getItem(NON_EXISTING_ITEM_ID)
      .execute();
    ErrorResponse errorResponse = client.parseError(getItemResponse);

    // expect error response with 404 error
    assertNotNull(errorResponse);
    assertEquals(errorResponse.getCode(), 404);
  }

  @Test
  void getItem_invalidItemId_errorResponse400() throws IOException {
    String invalidItemId = "_";
    Response<ItemResponse> getItemResponse = client.service().getItem(invalidItemId).execute();
    ErrorResponse errorResponse = client.parseError(getItemResponse);

    // expect error response with 400 error
    assertNotNull(errorResponse);
    assertEquals(errorResponse.getCode(), 400);
  }

}
