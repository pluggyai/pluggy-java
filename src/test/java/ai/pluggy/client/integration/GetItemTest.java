package ai.pluggy.client.integration;

import static ai.pluggy.client.integration.helper.ItemHelper.ITEM_FINISH_STATUSES;
import static ai.pluggy.client.integration.helper.ItemHelper.NON_EXISTING_ITEM_ID;
import static ai.pluggy.client.integration.helper.ItemHelper.createItem;
import static ai.pluggy.client.integration.helper.ItemHelper.getItemStatus;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ai.pluggy.client.integration.util.Poller;
import ai.pluggy.client.request.ParametersMap;
import ai.pluggy.client.response.ErrorResponse;
import ai.pluggy.client.response.ItemResponse;
import java.io.IOException;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import retrofit2.Response;

public class GetItemTest extends BaseApiIntegrationTest {

  @Test
  void getItem_existingItem_ok() throws IOException {
    // ensure item exists
    Integer connectorId = 0;
    ItemResponse item = createItem(client, connectorId);
    assertNotNull(item);

    String createdItemId = item.getId();

    // get item by id
    ItemResponse itemResponse = client.service().getItem(createdItemId).execute().body();

    // expect item response id to match requested
    assertNotNull(itemResponse);
    assertEquals(itemResponse.getId(), createdItemId);

    this.getItemsIdCreated().add(item.getId());
  }

  @SneakyThrows
  @Test
  void getItem_validItemWithInvalidCredentials_finishesWithLoginError() throws IOException {
    // precondition: ensure item with bad credentials params exists
    Integer connectorId = 0;
    ParametersMap validParametersBadCredentialsMap = ParametersMap
      .map("user", "_bad_user_")
      .with("password", "_bad_password_");

    ItemResponse itemWithBadCredentials = createItem(client, connectorId,
      validParametersBadCredentialsMap);
    assertNotNull(itemWithBadCredentials);

    // wait for item execution to finish.
    Poller.pollRequestUntil(
      () -> getItemStatus(client, itemWithBadCredentials.getId()),
      (ItemResponse itemStatusResponse) ->
        ITEM_FINISH_STATUSES.indexOf(itemStatusResponse.getStatus()) > 0,
      500, 35000
    );

    // expect item to be finished with login error status & code
    String expectedItemStatus = "LOGIN_ERROR";
    String expectedLoginErrorCode = "INVALID_CREDENTIALS";

    Response<ItemResponse> getItemFinishedResponse = client.service()
      .getItem(itemWithBadCredentials.getId()).execute();
    assertTrue(getItemFinishedResponse.isSuccessful());

    ItemResponse itemFinishedResponse = getItemFinishedResponse.body();
    assertNotNull(itemFinishedResponse);
    assertEquals(itemFinishedResponse.getStatus(), expectedItemStatus);

    assertNotNull(itemFinishedResponse.getError());
    assertEquals(itemFinishedResponse.getError().getCode(), expectedLoginErrorCode);

    this.getItemsIdCreated().add(itemWithBadCredentials.getId());
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
