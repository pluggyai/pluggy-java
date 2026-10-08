package ai.pluggy.client.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ai.pluggy.client.auth.EncryptedParametersInterceptor;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okio.Buffer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * The interceptor RSA-encrypts item credentials only; other bodies that carry a {@code parameters}
 * field must reach the API untouched.
 */
public class EncryptedParametersInterceptorTest {

  private static final MediaType JSON = MediaType.parse("application/json");

  private OkHttpClient httpClient;
  private String sentBody;

  @BeforeEach
  void setUp() throws NoSuchAlgorithmException {
    KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
    generator.initialize(2048);
    String publicKey = "-----BEGIN PUBLIC KEY-----\n"
      + Base64.getEncoder().encodeToString(generator.generateKeyPair().getPublic().getEncoded())
      + "\n-----END PUBLIC KEY-----";

    httpClient = new OkHttpClient.Builder()
      .addInterceptor(new EncryptedParametersInterceptor(publicKey))
      // capture what the encryption interceptor lets through, so no request leaves the process
      .addInterceptor(chain -> {
        Buffer buffer = new Buffer();
        chain.request().body().writeTo(buffer);
        sentBody = buffer.readUtf8();
        return new okhttp3.Response.Builder()
          .request(chain.request())
          .protocol(Protocol.HTTP_1_1)
          .code(200)
          .message("OK")
          .body(ResponseBody.create("{}", JSON))
          .build();
      })
      .build();
  }

  private JsonObject post(String path, String body) throws IOException {
    Request request = new Request.Builder()
      .url("https://api.pluggy.ai" + path)
      .post(RequestBody.create(body, JSON))
      .build();
    httpClient.newCall(request).execute().close();
    return JsonParser.parseString(sentBody).getAsJsonObject();
  }

  @Test
  void createItem_parametersAreEncrypted() throws IOException {
    JsonObject sent = post("/items", "{\"connectorId\":0,\"parameters\":{\"user\":\"u\"}}");

    assertTrue(sent.get("parameters").isJsonPrimitive());
    assertEquals(0, sent.get("connectorId").getAsInt());
  }

  @Test
  void smartTransferPreauthorization_parametersAreSentAsIs() throws IOException {
    JsonObject sent = post("/smart-transfers/preauthorizations",
      "{\"connectorId\":612,\"parameters\":{\"cpf\":\"41679949500\"},\"recipientIds\":[]}");

    assertTrue(sent.get("parameters").isJsonObject());
    assertEquals("41679949500", sent.getAsJsonObject("parameters").get("cpf").getAsString());
  }

  @Test
  void itemRequestWithoutParameters_isSentAsIs() throws IOException {
    JsonObject sent = post("/items/item-1/mfa", "{\"token\":\"123456\"}");

    assertEquals("123456", sent.get("token").getAsString());
  }
}
