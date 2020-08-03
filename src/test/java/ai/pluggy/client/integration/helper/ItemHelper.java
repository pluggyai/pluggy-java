package ai.pluggy.client.integration.helper;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ai.pluggy.client.PluggyClient;
import ai.pluggy.client.request.CreateItemRequest;
import ai.pluggy.client.request.ParametersMap;
import ai.pluggy.client.response.ItemResponse;
import lombok.SneakyThrows;
import retrofit2.Response;

public class ItemHelper {

  @SneakyThrows
  public static ItemResponse createItem(PluggyClient client, Integer connectorId) {
    // create item params
    ParametersMap parametersMap = ParametersMap.map("user", "asd")
      .with("password", "qwe")
      .with("cuit", "20-34232323-2");

    // run request with 'connectorId', 'parameters' params
    CreateItemRequest createItemRequest = new CreateItemRequest(connectorId, parametersMap);

    Response<ItemResponse> itemRequestResponse = client.service()
      .createItem(createItemRequest)
      .execute();
    
    assertTrue(itemRequestResponse.isSuccessful());
    ItemResponse itemResponse = itemRequestResponse.body();
    assertNotNull(itemResponse);

    return itemResponse;
  }


}
