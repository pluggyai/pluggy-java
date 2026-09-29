package ai.pluggy.client.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ai.pluggy.client.PluggyClient;
import ai.pluggy.client.request.ItemResourcesSearchRequest;
import ai.pluggy.client.response.ItemResource;
import ai.pluggy.client.response.ItemResourceStatus;
import ai.pluggy.client.response.ItemResourcesResponse;
import ai.pluggy.client.response.ItemResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.Date;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import retrofit2.Response;

public class ItemResourcesTest {

  private static final String ITEM_ID = "d0e8448e-0156-4b4a-ae6c-3e2a6d9bff5c";

  private PluggyClient client;
  private Request lastRequest;
  private String responseJson;

  @BeforeEach
  void setUp() {
    PluggyClient.PluggyClientBuilder builder = PluggyClient.builder()
      .clientIdAndSecret("client-id", "client-secret")
      .noAuthInterceptor();
    // answer every request with a canned body, so no request leaves the process
    builder.okHttpClientBuilder().addInterceptor(chain -> {
      lastRequest = chain.request();
      return new okhttp3.Response.Builder()
        .request(chain.request())
        .protocol(Protocol.HTTP_1_1)
        .code(200)
        .message("OK")
        .body(ResponseBody.create(responseJson, MediaType.parse("application/json")))
        .build();
    });
    client = builder.build();
  }

  @Test
  void getItemResources_withFilters_sendsQueryAndParsesPage() throws IOException {
    responseJson = "{\"page\":2,\"total\":52,\"totalPages\":2,\"results\":["
      + "{\"resourceId\":\"6c2d8b41-7e5a-4f03-b19d-8a4e2c7f5b60\","
      + "\"type\":\"CREDIT_CARD_ACCOUNT\",\"status\":\"PENDING_AUTHORISATION\"},"
      + "{\"resourceId\":\"a3e71d92-5b48-4c6f-8e20-1d9c3b7a4e50\","
      + "\"type\":\"FUND\",\"status\":\"PENDING_AUTHORISATION\"}]}";

    ItemResourcesSearchRequest request = new ItemResourcesSearchRequest()
      .page(2)
      .pageSize(50)
      .status(ItemResourceStatus.PENDING_AUTHORISATION);
    Response<ItemResourcesResponse> response = client.service()
      .getItemResources(ITEM_ID, request)
      .execute();

    assertEquals("GET", lastRequest.method());
    assertEquals("/items/" + ITEM_ID + "/resources", lastRequest.url().encodedPath());
    assertEquals("2", lastRequest.url().queryParameter("page"));
    assertEquals("50", lastRequest.url().queryParameter("pageSize"));
    assertEquals("PENDING_AUTHORISATION", lastRequest.url().queryParameter("status"));

    assertTrue(response.isSuccessful());
    ItemResourcesResponse page = response.body();
    assertNotNull(page);
    assertEquals(2, page.getPage());
    assertEquals(52, page.getTotal());
    assertEquals(2, page.getTotalPages());
    assertEquals(2, page.getResults().size());

    ItemResource resource = page.getResults().get(0);
    assertEquals("6c2d8b41-7e5a-4f03-b19d-8a4e2c7f5b60", resource.getResourceId());
    assertEquals("CREDIT_CARD_ACCOUNT", resource.getType());
    assertEquals(ItemResourceStatus.PENDING_AUTHORISATION, resource.getStatus());
  }

  @Test
  void getItemResources_withoutFilters_sendsNoQueryAndParsesEmptyPage() throws IOException {
    responseJson = "{\"page\":1,\"total\":0,\"totalPages\":0,\"results\":[]}";

    Response<ItemResourcesResponse> response = client.service()
      .getItemResources(ITEM_ID)
      .execute();

    assertEquals("/items/" + ITEM_ID + "/resources", lastRequest.url().encodedPath());
    assertNull(lastRequest.url().query());

    ItemResourcesResponse page = response.body();
    assertNotNull(page);
    assertEquals(0, page.getTotal());
    assertTrue(page.getResults().isEmpty());
  }

  @Test
  void getItemResources_unknownTypeAndStatus_doNotFailToParse() throws IOException {
    responseJson = "{\"page\":1,\"total\":1,\"totalPages\":1,\"results\":["
      + "{\"resourceId\":\"1f9a5e0c-3d2b-4a1e-9c7f-2b8d6e4a1c30\","
      + "\"type\":\"EXCHANGE\",\"status\":\"SOME_FUTURE_STATUS\"}]}";

    ItemResourcesResponse page = client.service().getItemResources(ITEM_ID).execute().body();

    assertNotNull(page);
    ItemResource resource = page.getResults().get(0);
    // an undocumented type is passed through as-is
    assertEquals("EXCHANGE", resource.getType());
    // a status added after this SDK release deserializes as null, like every SDK enum
    assertNull(resource.getStatus());
  }

  @Test
  void getItem_resourcesFields_parsed() throws IOException {
    responseJson = "{\"id\":\"" + ITEM_ID + "\","
      + "\"resourcesCollectedAt\":\"2026-09-01T12:30:00.000Z\","
      + "\"hasResourcesPendingAuthorization\":true}";

    ItemResponse item = client.service().getItem(ITEM_ID).execute().body();

    assertNotNull(item);
    assertEquals(Date.from(Instant.parse("2026-09-01T12:30:00Z")), item.getResourcesCollectedAt());
    assertTrue(item.getHasResourcesPendingAuthorization());
  }

  @Test
  void getItem_resourcesNeverCollected_bothNull() throws IOException {
    responseJson = "{\"id\":\"" + ITEM_ID + "\","
      + "\"resourcesCollectedAt\":null,"
      + "\"hasResourcesPendingAuthorization\":null}";

    ItemResponse item = client.service().getItem(ITEM_ID).execute().body();

    assertNotNull(item);
    assertNull(item.getResourcesCollectedAt());
    assertNull(item.getHasResourcesPendingAuthorization());
  }
}
