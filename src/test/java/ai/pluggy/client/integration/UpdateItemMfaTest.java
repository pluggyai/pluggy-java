package ai.pluggy.client.integration;

import static ai.pluggy.client.integration.helper.ItemHelper.*;
import static ai.pluggy.client.integration.util.AssertionsUtils.assertSuccessful;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ai.pluggy.client.integration.util.Poller;
import ai.pluggy.client.request.UpdateItemMfaRequest;
import ai.pluggy.client.response.CredentialLabel;
import ai.pluggy.client.response.ItemResponse;
import java.util.Objects;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import retrofit2.Response;

public class UpdateItemMfaTest extends BaseApiIntegrationTest {

  @SneakyThrows
  @Test
  void updateItemMfa_afterCreated_withMfaCredential_ok() {
    // precondition: an MFA item already exists
    ItemResponse createdItemResponse = createPluggyBankMfaSecondStepItem(client);
    
    System.out.println(createdItemResponse.getStatus());

    // wait for creation finish and waiting for MFA (status: "WAITING_USER_INPUT") before updating, to prevent update request error 400.
    Poller.pollRequestUntil(
      () -> getItemStatus(client, createdItemResponse.getId()),
      (ItemResponse itemStatusResponse) -> {
        System.out.println(itemStatusResponse.getStatus());
        return Objects
        .equals(itemStatusResponse.getStatus(), "WAITING_USER_INPUT");
      },
      500, 45000
    );

    ItemResponse itemMfaStatus = getItemStatus(client, createdItemResponse.getId());
    assertEquals(itemMfaStatus.getStatus(), "WAITING_USER_INPUT");

    // extract mfa credential field name, to build the update MFA request
    CredentialLabel mfaCredentialParameter = itemMfaStatus.getParameter();
    assertNotNull(mfaCredentialParameter);
    
    String mfaCredentialFieldName = mfaCredentialParameter.getName();
    assertNotNull(mfaCredentialFieldName);

    // build update mfa item request
    UpdateItemMfaRequest updateItemMfaRequest = new UpdateItemMfaRequest()
      .with(mfaCredentialFieldName, "1");

    // run update item with mfa param
    Response<ItemResponse> updateItemMfaResponse = client.service()
      .updateItemSendMfa(createdItemResponse.getId(), updateItemMfaRequest)
      .execute();

    // expect response (to empty params request) to be successful
    assertSuccessful(updateItemMfaResponse, client);
    assertTrue(updateItemMfaResponse.isSuccessful());
    ItemResponse updatedItem = updateItemMfaResponse.body();

    Poller.pollRequestUntil(
      () -> getItemStatus(client, createdItemResponse.getId()),
      (ItemResponse itemStatusResponse) -> Objects
        .equals(itemStatusResponse.getStatus(), "UPDATING"),
      500, 45000
    );

    // expect item to be status = "UPDATING"
    assertNotNull(updatedItem);
    ItemResponse updatingItemStatus = getItemStatus(client, createdItemResponse.getId());
    assertEquals(updatingItemStatus.getStatus(), "UPDATING");
  }


}
