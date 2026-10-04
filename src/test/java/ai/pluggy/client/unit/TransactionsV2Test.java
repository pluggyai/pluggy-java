package ai.pluggy.client.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ai.pluggy.client.PluggyClient;
import ai.pluggy.client.request.TransactionsCursorSearchRequest;
import ai.pluggy.client.response.AccountBalance;
import ai.pluggy.client.response.CreditCardAccountPaymentType;
import ai.pluggy.client.response.Transaction;
import ai.pluggy.client.response.TransactionCreditCardMetadata;
import ai.pluggy.client.response.TransactionsCursorResponse;
import java.io.IOException;
import java.util.Arrays;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import retrofit2.Response;

public class TransactionsV2Test {

  private static final String ACCOUNT_ID = "03fc3a6f-2b5e-4e8e-9f4c-1a2b3c4d5e6f";

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
  void getTransactionsV2_parsesPageAndCreditCardMetadata() throws IOException {
    responseJson = "{\"results\":[{\"id\":\"t-1\",\"accountId\":\"" + ACCOUNT_ID + "\","
      + "\"amount\":-120.5,\"date\":\"2026-09-10T00:00:00.000Z\","
      + "\"creditCardMetadata\":{\"installmentNumber\":1,\"totalInstallments\":3,"
      + "\"paymentType\":\"INSTALLMENT\",\"billPostDate\":\"2026-09-15\","
      + "\"transactionDateTime\":\"2026-09-10T14:32:11.000Z\","
      + "\"billForecastDate\":\"2026-10-05\"}}],"
      + "\"next\":\"?accountId=" + ACCOUNT_ID + "&dateFrom=2026-09-01&after=abc%2B123%3D%3D\"}";

    Response<TransactionsCursorResponse> response = client.service()
      .getTransactionsV2(ACCOUNT_ID)
      .execute();

    assertEquals("GET", lastRequest.method());
    assertEquals("/v2/transactions", lastRequest.url().encodedPath());
    assertEquals(ACCOUNT_ID, lastRequest.url().queryParameter("accountId"));
    assertEquals(1, lastRequest.url().querySize());

    TransactionsCursorResponse page = response.body();
    assertNotNull(page);
    assertEquals(1, page.getResults().size());
    assertEquals("abc+123==", page.getNextCursor());
    assertTrue(page.hasNext());

    Transaction transaction = page.getResults().get(0);
    TransactionCreditCardMetadata metadata = transaction.getCreditCardMetadata();
    assertEquals(CreditCardAccountPaymentType.INSTALLMENT, metadata.getPaymentType());
    assertEquals("2026-09-15", metadata.getBillPostDate());
    assertEquals("2026-09-10T14:32:11.000Z", metadata.getTransactionDateTime());
    assertEquals("2026-10-05", metadata.getBillForecastDate());
    assertEquals(3, metadata.getTotalInstallments());
  }

  @Test
  void getTransactionsV2_lastPage_hasNoCursor_andMissingFieldsAreNull() throws IOException {
    responseJson = "{\"results\":[{\"id\":\"t-2\",\"creditCardMetadata\":"
      + "{\"paymentType\":\"SOME_FUTURE_TYPE\",\"billPostDate\":null}}],\"next\":null}";

    TransactionsCursorResponse page = client.service()
      .getTransactionsV2(ACCOUNT_ID)
      .execute()
      .body();

    assertNotNull(page);
    assertNull(page.getNext());
    assertNull(page.getNextCursor());
    assertFalse(page.hasNext());
    TransactionCreditCardMetadata metadata = page.getResults().get(0).getCreditCardMetadata();
    // a payment type added after this SDK release deserializes as null, like every SDK enum
    assertNull(metadata.getPaymentType());
    assertNull(metadata.getBillPostDate());
    assertNull(metadata.getTransactionDateTime());
  }

  @Test
  void getTransactionsV2_withFilters_sendsOnlyWhitelistedParams() throws IOException {
    responseJson = "{\"results\":[],\"next\":null}";

    TransactionsCursorSearchRequest request = new TransactionsCursorSearchRequest()
      .ids(Arrays.asList("a", "b"))
      .dateFrom("2026-09-01")
      .dateTo("2026-09-30")
      .after("abc+123==");
    client.service().getTransactionsV2(ACCOUNT_ID, request).execute();

    assertEquals("/v2/transactions", lastRequest.url().encodedPath());
    assertEquals(ACCOUNT_ID, lastRequest.url().queryParameter("accountId"));
    assertEquals("a,b", lastRequest.url().queryParameter("ids"));
    assertEquals("2026-09-01", lastRequest.url().queryParameter("dateFrom"));
    assertEquals("2026-09-30", lastRequest.url().queryParameter("dateTo"));
    // the decoded cursor is re-encoded on the wire and round-trips intact
    assertEquals("abc+123==", lastRequest.url().queryParameter("after"));
    assertEquals(5, lastRequest.url().querySize());
    assertNull(lastRequest.url().queryParameter("page"));
    assertNull(lastRequest.url().queryParameter("pageSize"));
  }

  @Test
  void cursorSearchRequest_dateFromAndCreatedAtFrom_areMutuallyExclusive() {
    assertThrows(IllegalArgumentException.class, () -> new TransactionsCursorSearchRequest()
      .dateFrom("2026-09-01")
      .createdAtFrom("2026-09-01T00:00:00.000Z"));
    assertThrows(IllegalArgumentException.class, () -> new TransactionsCursorSearchRequest()
      .createdAtFrom("2026-09-01T00:00:00.000Z")
      .dateFrom("2026-09-01"));
  }

  @Test
  void parseAfterCursor_handlesEdgeCases() {
    assertNull(TransactionsCursorResponse.parseAfterCursor(null));
    assertNull(TransactionsCursorResponse.parseAfterCursor(""));
    assertNull(TransactionsCursorResponse.parseAfterCursor("?accountId=x"));
    assertNull(TransactionsCursorResponse.parseAfterCursor("?accountId=x&after="));
    assertEquals("c1", TransactionsCursorResponse.parseAfterCursor("?after=c1"));
    assertEquals("c1", TransactionsCursorResponse.parseAfterCursor("after=c1&accountId=x"));
    assertEquals("c1",
      TransactionsCursorResponse.parseAfterCursor("https://api.pluggy.ai/v2/transactions?a=1&after=c1"));
    // a param that merely ends in "after" is not the cursor
    assertNull(TransactionsCursorResponse.parseAfterCursor("?notafter=c1"));
  }

  @Test
  void getAccountBalance_parsesBalance() throws IOException {
    responseJson = "{\"balance\":1523.47,\"currencyCode\":\"BRL\","
      + "\"updateDateTime\":\"2026-10-01T10:15:00.000Z\"}";

    AccountBalance balance = client.service().getAccountBalance(ACCOUNT_ID).execute().body();

    assertEquals("GET", lastRequest.method());
    assertEquals("/accounts/" + ACCOUNT_ID + "/balance", lastRequest.url().encodedPath());
    assertNotNull(balance);
    assertEquals(1523.47, balance.getBalance());
    assertEquals("BRL", balance.getCurrencyCode());
    assertEquals("2026-10-01T10:15:00.000Z", balance.getUpdateDateTime());
  }
}
