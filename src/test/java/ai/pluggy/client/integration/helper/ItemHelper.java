package ai.pluggy.client.integration.helper;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import ai.pluggy.client.PluggyClient;
import ai.pluggy.client.request.CreateItemRequest;
import ai.pluggy.client.request.ParametersMap;
import ai.pluggy.client.response.ErrorResponse;
import ai.pluggy.client.response.ItemResponse;
import java.util.Arrays;
import java.util.List;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import retrofit2.Response;

@Log4j2
public class ItemHelper {

  // an UUID that doesn't belong to any existing Item
  public static final String NON_EXISTING_ITEM_ID = "ab9f7a00-7d45-458b-b288-4923e18a9e69";

  public static final Integer PLUGGY_BANK_CONNECTOR_ID = 0;

  // Possible item statuses that indicate execution finished
  public static final List<String> ITEM_FINISH_STATUSES = Arrays
    .asList("FINISHED", "OUTDATED", "LOGIN_ERROR");

  @SneakyThrows
  public static ItemResponse createItem(PluggyClient client, Integer connectorId) {
    ParametersMap invalidParametersMap = ParametersMap.map("user", "asd")
      .with("password", "qwe")
      .with("dni", "123123123")
      .with("cuit", "20-34232323-2");

    return createItem(client, connectorId, invalidParametersMap);
  }

  @SneakyThrows
  public static ItemResponse createItem(PluggyClient client, Integer connectorId,
    ParametersMap parametersMap) {
    log.info("Creating item execution for connector id '" + connectorId + "'...");
    // run request with 'connectorId', 'parameters' params
    CreateItemRequest createItemRequest = new CreateItemRequest(connectorId, parametersMap);

    Response<ItemResponse> itemRequestResponse = client.service()
      .createItem(createItemRequest)
      .execute();

    assertTrue(itemRequestResponse.isSuccessful());
    ItemResponse itemResponse = itemRequestResponse.body();
    assertNotNull(itemResponse);
    log.info("Created Item execution, id '" + itemResponse.getId() + "' "
      + "(connector id '" + connectorId + "'). ");

    return itemResponse;
  }

  public static ItemResponse createPluggyBankItem(PluggyClient client) {
    ParametersMap parametersMap = ParametersMap
      .map("user", "user-ok")
      .with("password", "password-ok");
    ItemResponse pluggyBankExecution = createItem(client, PLUGGY_BANK_CONNECTOR_ID, parametersMap);
    return pluggyBankExecution;
  }

  @SneakyThrows
  public static ItemResponse getItemStatus(PluggyClient client, String itemId) {
    // get item by id
    Response<ItemResponse> itemResponse = client.service().getItem(itemId).execute();

    // assert getItem response is successful
    if (!itemResponse.isSuccessful()) {
      ErrorResponse errorResponse = client.parseError(itemResponse);
      fail(String.format(
        "getItem response for execution id '%s' failed unexpectedly, error response: %s",
        itemId, errorResponse)
      );
    }

    ItemResponse itemResponseParsed = itemResponse.body();
    // expect item response to exist
    assertNotNull(itemResponseParsed, String.format("got a null itemResponse for id '%s'", itemId));

    return itemResponseParsed;
  }


}
