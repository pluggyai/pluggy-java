package ai.pluggy.client.integration;

import ai.pluggy.client.response.ItemResponse;
import ai.pluggy.client.response.ItemsResponse;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static ai.pluggy.client.integration.helper.ItemHelper.*;
import static org.junit.jupiter.api.Assertions.*;

public class GetItemsTest extends BaseApiIntegrationTest {

  @Test
  void getItems_existingItems_ok() throws IOException {
    
    //before run test remove all items
    this.removeAllItems();
    
    // create two items
    Integer connectorId = 0;
    ItemResponse itemOne = createItem(client, connectorId);
    assertNotNull(itemOne);

    ItemResponse itemTwo = createItem(client, connectorId);
    assertNotNull(itemTwo);

    // get all items
    ItemsResponse itemsResponse = client.service().getItems().execute().body();

    // expect item response have two items
    assertNotNull(itemsResponse);
    assertEquals(itemsResponse.getResults().length, 2);
  }

}
