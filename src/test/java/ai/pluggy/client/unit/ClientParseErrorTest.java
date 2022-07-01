package ai.pluggy.client.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ai.pluggy.client.PluggyClient;
import ai.pluggy.client.response.ErrorResponse;
import com.google.gson.JsonParseException;
import java.io.IOException;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import retrofit2.Response;

public class ClientParseErrorTest {

  private PluggyClient client;

  @BeforeEach
  void setUp() {
    client = PluggyClient.builder()
      .clientIdAndSecret("client-id", "client-secret")
      .build();
  }

  @Test
  void clientParseError_jsonErrorResponse_ok() throws IOException {
    // simulate error response
    String errorMessage = "An update is already in progress, wait until the last execution ends";
    Integer errorCode = 400;
    String errorJson = "{\"message\":\"" + errorMessage + "\",\"code\":" + errorCode + "}";
    Response<Object> errorResponse = Response
      .error(400, ResponseBody.create(errorJson, MediaType.parse("application/json")));

    // expect parsed error response
    ErrorResponse errorResponseParsed = client.parseError(errorResponse);

    assertNotNull(errorResponseParsed);
    assertEquals(errorMessage, errorResponseParsed.getMessage());
    assertEquals(errorCode, errorResponseParsed.getCode());
  }

  @Test
  void clientParseError_malformedJsonResponse_returnsErrorResponse() throws IOException {
    // simulate error response
    String malformedErrorJson = "{";
    Response<Object> errorResponse = Response
      .error(400, ResponseBody.create(malformedErrorJson, MediaType.parse("application/json")));

    ErrorResponse parsedErrorResponse = client.parseError(errorResponse);
    assertNotNull(parsedErrorResponse);
    assertNotNull(parsedErrorResponse.getMessage());
    assertTrue(parsedErrorResponse.getMessage().contains(malformedErrorJson));
  }
}
