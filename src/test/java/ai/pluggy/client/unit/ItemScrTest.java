package ai.pluggy.client.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import ai.pluggy.client.PluggyClient;
import ai.pluggy.client.response.ErrorResponse;
import ai.pluggy.client.response.ScrDatabase;
import ai.pluggy.client.response.ScrOperation;
import ai.pluggy.client.response.ScrResponse;
import java.io.IOException;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import retrofit2.Response;

public class ItemScrTest {

  private static final String SCR_JSON = "{\"dtbConsult\":\"202604 a 202607\","
    + "\"cdCli\":\"12345678901\",\"tpCli\":\"1\","
    + "\"lsDtb\":[{\"dtb\":202607,\"docProc\":\"99.5\",\"volProc\":\"98.1\",\"qtdIfs\":2,"
    + "\"qtdCongFinc\":1,\"dtbIniRel\":\"2010-03-01\",\"coobAss\":0,\"coobRec\":150.25,"
    + "\"lsOp\":[{\"mod\":\"0203\",\"oriRec\":\"0100\",\"indx\":\"11\",\"varCamb\":\"790\","
    + "\"subJDisc\":\"D\",\"resVenc\":{\"v110\":1000.5,\"v120\":250,\"v320\":10},"
    + "\"lsGar\":[{\"tp\":\"0101\",\"qtd\":1}],"
    + "\"lsInfAd\":[{\"tp\":\"01\",\"cd\":\"0001\",\"qtd\":3}]}]},"
    + "{\"dtb\":202604,\"msg\":\"Base date not available for consultation\"}],"
    + "\"listaDeMensagensDeValidacao\":[{\"codigo\":\"W01\",\"mensagem\":\"Partial data\"}]}";

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
  void getItemScr_withoutRange_sendsNoQueryAndParsesPayload() throws IOException {
    responseJson = SCR_JSON;

    Response<ScrResponse> response = client.service().getItemScr("item-1").execute();

    assertEquals("GET", lastRequest.method());
    assertEquals("/items/item-1/scr", lastRequest.url().encodedPath());
    assertEquals(0, lastRequest.url().querySize());

    ScrResponse scr = response.body();
    assertNotNull(scr);
    assertEquals("202604 a 202607", scr.getDtbConsult());
    assertEquals("12345678901", scr.getCdCli());
    assertEquals("1", scr.getTpCli());
    assertEquals(2, scr.getLsDtb().size());

    ScrDatabase base = scr.getLsDtb().get(0);
    assertEquals(202607, base.getDtb());
    assertEquals("99.5", base.getDocProc());
    assertEquals("98.1", base.getVolProc());
    assertEquals(2, base.getQtdIfs());
    assertEquals(1, base.getQtdCongFinc());
    assertEquals("2010-03-01", base.getDtbIniRel());
    assertEquals(0.0, base.getCoobAss());
    assertEquals(150.25, base.getCoobRec());

    ScrOperation op = base.getLsOp().get(0);
    assertEquals("0203", op.getMod());
    assertEquals("0100", op.getOriRec());
    assertEquals("11", op.getIndx());
    assertEquals("790", op.getVarCamb());
    assertEquals("D", op.getSubJDisc());
    assertEquals(1000.5, op.getResVenc().getV110());
    assertEquals(250.0, op.getResVenc().getV120());
    assertEquals(10.0, op.getResVenc().getV320());
    assertNull(op.getResVenc().getV20());
    assertEquals("0101", op.getLsGar().get(0).getTp());
    assertEquals(1, op.getLsGar().get(0).getQtd());
    assertEquals("01", op.getLsInfAd().get(0).getTp());
    assertEquals("0001", op.getLsInfAd().get(0).getCd());
    assertEquals(3, op.getLsInfAd().get(0).getQtd());

    ScrDatabase empty = scr.getLsDtb().get(1);
    assertEquals("Base date not available for consultation", empty.getMsg());
    assertNull(empty.getLsOp());

    assertEquals("W01", scr.getListaDeMensagensDeValidacao().get(0).getCodigo());
    assertEquals("Partial data", scr.getListaDeMensagensDeValidacao().get(0).getMensagem());
  }

  @Test
  void getItemScr_withRange_sendsFromAndTo() throws IOException {
    responseJson = SCR_JSON;

    client.service().getItemScr("item-1", "202604", "202607").execute();

    assertEquals("/items/item-1/scr", lastRequest.url().encodedPath());
    assertEquals(2, lastRequest.url().querySize());
    assertEquals("202604", lastRequest.url().queryParameter("from"));
    assertEquals("202607", lastRequest.url().queryParameter("to"));
  }

  @Test
  void getItemScr_withNullBounds_omitsThem() throws IOException {
    responseJson = SCR_JSON;

    client.service().getItemScr("item-1", null, "202607").execute();

    assertEquals(1, lastRequest.url().querySize());
    assertNull(lastRequest.url().queryParameter("from"));
    assertEquals("202607", lastRequest.url().queryParameter("to"));
  }

  @Test
  void getItemScr_featureNotEnabled_parsesErrorCode() throws IOException {
    responseCode = 403;
    responseJson = "{\"code\":403,\"codeDescription\":\"SCR_FEATURE_NOT_ENABLED\","
      + "\"message\":\"This client is not enabled to query SCR data.\"}";

    Response<ScrResponse> response = client.service().getItemScr("item-1").execute();

    assertFalse(response.isSuccessful());
    assertEquals(403, response.code());
    ErrorResponse error = client.parseError(response);
    assertEquals(403, error.getCode());
    assertEquals("SCR_FEATURE_NOT_ENABLED", error.getCodeDescription());
    assertEquals("This client is not enabled to query SCR data.", error.getMessage());
  }

  @Test
  void getItemScr_itemNotSupported_parsesErrorCode() throws IOException {
    responseCode = 422;
    responseJson = "{\"code\":422,\"codeDescription\":\"SCR_ITEM_NOT_SUPPORTED\","
      + "\"message\":\"SCR is only available for Open Finance items that have a known CPF or CNPJ.\"}";

    Response<ScrResponse> response = client.service().getItemScr("item-1").execute();

    assertEquals(422, response.code());
    assertEquals("SCR_ITEM_NOT_SUPPORTED", client.parseError(response).getCodeDescription());
  }
}
