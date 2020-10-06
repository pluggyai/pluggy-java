package ai.pluggy.client.integration;

import static ai.pluggy.client.integration.helper.ItemHelper.createItem;
import static ai.pluggy.client.integration.helper.ItemHelper.getItemStatus;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import ai.pluggy.client.integration.util.Poller;
import ai.pluggy.client.request.ParametersMap;
import ai.pluggy.client.response.ErrorResponse;
import ai.pluggy.client.response.IdentityResponse;
import ai.pluggy.client.response.ItemResponse;
import java.util.Objects;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import retrofit2.Response;

public class GetIdentityTest extends BaseApiIntegrationTest {

  @SneakyThrows
  @Test
  void getIdentityByItemId_existingItemId_waitUntilCompleted_ok() {
    // ensure item exists
    Integer connectorId = 0;
    ParametersMap validCredentials = ParametersMap.map("user", "user-ok")
      .with("password", "password-ok");
    ItemResponse item = createItem(client, connectorId, validCredentials);
    assertNotNull(item);

    String existingItemId = item.getId();

    // wait until item completed (item status: "UPDATED")
    Poller.pollRequestUntil(
      () -> getItemStatus(client, item.getId()),
      (ItemResponse itemResponse) -> Objects.equals(itemResponse.getStatus(), "UPDATED"),
      500, 30000
    );

    // get identity by item id
    Response<IdentityResponse> identityByItemIdResponse = client.service()
      .getIdentityByItemId(existingItemId)
      .execute();

    // expect response to be OK (and not null)
    assertEquals(200, identityByItemIdResponse.code());

    IdentityResponse identityResponse = identityByItemIdResponse.body();
    assertNotNull(identityResponse);
  }

  @SneakyThrows
  @Test
  void getIdentityByItemId_existingItemId_beforeCompleted_fails() {
    // ensure item exists
    Integer connectorId = 0;
    ParametersMap validCredentials = ParametersMap.map("user", "user-ok")
      .with("password", "password-ok");
    ItemResponse item = createItem(client, connectorId, validCredentials);
    assertNotNull(item);

    String existingItemId = item.getId();

    // get identity by item id
    Response<IdentityResponse> identityByItemIdResponse = client.service()
      .getIdentityByItemId(existingItemId)
      .execute();

    // expect response to be 404 not found
    assertEquals(404, identityByItemIdResponse.code());

    ErrorResponse errorResponse = client.parseError(identityByItemIdResponse);
    assertEquals(404, errorResponse.getCode());
  }


  @SneakyThrows
  @Test
  void getIdentityById_existingIdentityOfExistingCompletedItemId_ok() {
    // ensure item exists
    Integer connectorId = 0;
    ParametersMap validCredentials = ParametersMap.map("user", "user-ok")
      .with("password", "password-ok");
    ItemResponse item = createItem(client, connectorId, validCredentials);
    assertNotNull(item);

    String existingItemId = item.getId();

    // wait until item completed (item status: "UPDATED")
    Poller.pollRequestUntil(
      () -> getItemStatus(client, existingItemId),
      (ItemResponse itemResponse) -> Objects.equals(itemResponse.getStatus(), "UPDATED"),
      500, 30000
    );

    // ensure identity by item id exists
    Response<IdentityResponse> identityByItemIdResponse = client.service()
      .getIdentityByItemId(existingItemId)
      .execute();

    IdentityResponse identityResponse = identityByItemIdResponse.body();
    String identityId = identityResponse.getId();
    assertNotNull(identityId);

    // get identity by id
    Response<IdentityResponse> identityByIdResponse = client.service().getIdentityById(identityId)
      .execute();

    // expect response to be OK and to match originally requested identity
    assertEquals(identityByIdResponse.code(), 200);
    IdentityResponse identityById = identityByIdResponse.body();
    assertNotNull(identityById);
    assertEquals(identityById.getId(), identityId);
    assertEquals(identityById.getFullName(), identityResponse.getFullName());
  }

}
