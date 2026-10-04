package ai.pluggy.client.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ai.pluggy.client.PluggyClient;
import ai.pluggy.client.request.ItemsCursorSearchRequest;
import ai.pluggy.client.response.ErrorResponse;
import ai.pluggy.client.response.ItemResponse;
import ai.pluggy.client.response.ItemsCursorResponse;
import java.io.IOException;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import retrofit2.Response;

public class ItemsV2Test {

  private PluggyClient client;
  private Request lastRequest;
  private int responseCode;
  private String responseJson;

  @BeforeEach
  void setUp() {
    responseCode = 200;
    PluggyClient.PluggyClientBuilder builder = PluggyClient.builder()
      .clientIdAndSecret("client-id", "client-secret")
      .noAuthInterceptor();
    // answer every request with a canned body, so no request leaves the process
    builder.okHttpClientBuilder().addInterceptor(chain -> {
      lastRequest = chain.request();
      return new okhttp3.Response.Builder()
        .request(chain.request())
        .protocol(Protocol.HTTP_1_1)
        .code(responseCode)
        .message(responseCode == 200 ? "OK" : "Error")
        .body(ResponseBody.create(responseJson, MediaType.parse("application/json")))
        .build();
    });
    client = builder.build();
  }

  @Test
  void getItems_parsesPageAndCursor() throws IOException {
    responseJson = "{\"results\":["
      + "{\"id\":\"item-2\",\"clientUserId\":\"user-1\",\"status\":\"UPDATED\","
      + "\"connector\":{\"id\":201,\"name\":\"Bank\"}},"
      + "{\"id\":\"item-1\",\"clientUserId\":\"user-1\",\"status\":\"LOGIN_ERROR\"}],"
      + "\"next\":\"?clientUserId=user-1&after=abc%2B123%3D%3D\"}";

    Response<ItemsCursorResponse> response = client.service().getItems().execute();

    assertEquals("GET", lastRequest.method());
    assertEquals("/v2/items", lastRequest.url().encodedPath());
    assertEquals(0, lastRequest.url().querySize());

    ItemsCursorResponse page = response.body();
    assertNotNull(page);
    assertEquals(2, page.getResults().size());
    ItemResponse newest = page.getResults().get(0);
    assertEquals("item-2", newest.getId());
    assertEquals("user-1", newest.getClientUserId());
    assertEquals(201, newest.getConnector().getId());
    assertEquals("abc+123==", page.getNextCursor());
    assertTrue(page.hasNext());
  }

  @Test
  void getItems_withFilters_sendsOnlyWhitelistedParams() throws IOException {
    responseJson = "{\"results\":[],\"next\":null}";

    ItemsCursorSearchRequest request = new ItemsCursorSearchRequest()
      .clientUserId("user 1")
      .connectorId(201)
      .after("abc+123==");
    client.service().getItems(request).execute();

    assertEquals("/v2/items", lastRequest.url().encodedPath());
    assertEquals("user 1", lastRequest.url().queryParameter("clientUserId"));
    assertEquals("201", lastRequest.url().queryParameter("connectorId"));
    // the decoded cursor is re-encoded on the wire and round-trips intact
    assertEquals("abc+123==", lastRequest.url().queryParameter("after"));
    assertEquals(3, lastRequest.url().querySize());
    assertNull(lastRequest.url().queryParameter("page"));
    assertNull(lastRequest.url().queryParameter("pageSize"));
  }

  @Test
  void getItems_lastPage_hasNoCursor() throws IOException {
    responseJson = "{\"results\":[{\"id\":\"item-1\"}],\"next\":null}";

    ItemsCursorResponse page = client.service().getItems().execute().body();

    assertNotNull(page);
    assertEquals(1, page.getResults().size());
    assertNull(page.getNext());
    assertNull(page.getNextCursor());
    assertFalse(page.hasNext());
  }

  @Test
  void getItems_featureNotEnabled_parsesErrorCode() throws IOException {
    responseCode = 403;
    responseJson = "{\"message\":\"This client is not enabled to list its items.\","
      + "\"code\":403,\"codeDescription\":\"LIST_ITEMS_FEATURE_NOT_ENABLED\"}";

    Response<ItemsCursorResponse> response = client.service().getItems().execute();

    assertFalse(response.isSuccessful());
    assertEquals(403, response.code());
    ErrorResponse error = client.parseError(response);
    assertEquals(403, error.getCode());
    assertEquals("LIST_ITEMS_FEATURE_NOT_ENABLED", error.getCodeDescription());
    assertEquals("This client is not enabled to list its items.", error.getMessage());
  }

  @Test
  void itemsCursorSearchRequest_validatesInput() {
    StringBuilder tooLong = new StringBuilder();
    for (int i = 0; i < 256; i++) {
      tooLong.append('x');
    }
    assertThrows(IllegalArgumentException.class,
      () -> new ItemsCursorSearchRequest().clientUserId(tooLong.toString()));
    assertThrows(IllegalArgumentException.class,
      () -> new ItemsCursorSearchRequest().connectorId(-1));
    assertThrows(IllegalArgumentException.class,
      () -> new ItemsCursorSearchRequest().after(null));
    assertEquals(0, new ItemsCursorSearchRequest().connectorId(0).getConnectorId());
  }
}
