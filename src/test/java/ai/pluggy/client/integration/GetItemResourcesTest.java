package ai.pluggy.client.integration;

import static ai.pluggy.client.integration.helper.ItemHelper.NON_EXISTING_ITEM_ID;
import static ai.pluggy.client.integration.helper.ItemHelper.createPluggyBankItem;
import static ai.pluggy.client.integration.util.AssertionsUtils.assertSuccessful;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ai.pluggy.client.response.ErrorResponse;
import ai.pluggy.client.response.ItemResourcesResponse;
import ai.pluggy.client.response.ItemResponse;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import retrofit2.Response;

public class GetItemResourcesTest extends BaseApiIntegrationTest {

  @Test
  void getItemResources_nonOpenFinanceItem_emptyPage() throws IOException {
    ItemResponse item = createPluggyBankItem(client);
    this.getItemsIdCreated().add(item.getId());

    Response<ItemResourcesResponse> response = client.service()
        .getItemResources(item.getId())
        .execute();

    assertSuccessful(response, client);
    ItemResourcesResponse page = response.body();
    assertNotNull(page);
    assertNotNull(page.getResults());
    assertTrue(page.getResults().isEmpty());
  }

  @Test
  void getItemResources_nonExistingItem_errorResponse404() throws IOException {
    Response<ItemResourcesResponse> response = client.service()
        .getItemResources(NON_EXISTING_ITEM_ID)
        .execute();
    ErrorResponse errorResponse = client.parseError(response);

    assertNotNull(errorResponse);
    assertEquals(404, errorResponse.getCode());
  }
}
