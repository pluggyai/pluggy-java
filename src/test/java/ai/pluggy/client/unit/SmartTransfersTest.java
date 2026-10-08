package ai.pluggy.client.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ai.pluggy.client.PluggyClient;
import ai.pluggy.client.request.CreateSmartTransferPaymentRequest;
import ai.pluggy.client.request.CreateSmartTransferPreauthorizationRequest;
import ai.pluggy.client.request.SmartTransferPreauthorizationParameter;
import ai.pluggy.client.request.SmartTransferPreauthorizationPaymentsSearchRequest;
import ai.pluggy.client.request.SmartTransferPreauthorizationsSearchRequest;
import ai.pluggy.client.response.ErrorResponse;
import ai.pluggy.client.response.PaymentRecipient;
import ai.pluggy.client.response.SmartTransferCallbackUrls;
import ai.pluggy.client.response.SmartTransferDataConsent;
import ai.pluggy.client.response.SmartTransferDataConsentStatus;
import ai.pluggy.client.response.SmartTransferPayment;
import ai.pluggy.client.response.SmartTransferPaymentStatus;
import ai.pluggy.client.response.SmartTransferPaymentsResponse;
import ai.pluggy.client.response.SmartTransferPeriodicLimit;
import ai.pluggy.client.response.SmartTransferPeriodicLimits;
import ai.pluggy.client.response.SmartTransferPreauthorization;
import ai.pluggy.client.response.SmartTransferPreauthorizationBalance;
import ai.pluggy.client.response.SmartTransferPreauthorizationConfiguration;
import ai.pluggy.client.response.SmartTransferPreauthorizationStatus;
import ai.pluggy.client.response.SmartTransferPreauthorizationsResponse;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.ResponseBody;
import okio.Buffer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import retrofit2.Response;

public class SmartTransfersTest {

  private static final String PREAUTHORIZATION_ID = "db97ad98-5b8f-4027-a248-6e112a73ced8";
  private static final String PAYMENT_ID = "0f6b1b6e-2c4a-4e0b-9d55-6a1f0f1c3e21";
  private static final String RECIPIENT_ID = "7b110544-dfcf-406a-bab7-b14f14e2d0c7";

  private static final String RECIPIENT_JSON = "{\"type\":\"BANK_ACCOUNT\","
    + "\"id\":\"" + RECIPIENT_ID + "\",\"name\":\"John Doe\",\"taxNumber\":\"11111111111\","
    + "\"isDefault\":false,"
    + "\"paymentInstitution\":{\"id\":\"abfd2a88-bc7b-407f-9fcc-395548ee6840\","
    + "\"name\":\"Banco XP S.A.\",\"tradeName\":\"BCO XP S.A.\",\"ispb\":\"33264668\","
    + "\"compe\":\"348\",\"createdAt\":\"2023-12-08T17:52:21.001Z\","
    + "\"updatedAt\":\"2023-12-08T17:52:21.001Z\"},"
    + "\"account\":{\"type\":\"CHECKING_ACCOUNT\",\"number\":\"1111111\",\"branch\":\"0001\"},"
    + "\"pixKey\":null,\"createdAt\":\"2024-07-31T15:55:47.886Z\","
    + "\"updatedAt\":\"2024-07-31T15:56:23.123Z\"}";

  private PluggyClient client;
  private Request lastRequest;
  private String lastRequestBody;
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
      lastRequestBody = null;
      if (lastRequest.body() != null) {
        Buffer buffer = new Buffer();
        lastRequest.body().writeTo(buffer);
        lastRequestBody = buffer.readUtf8();
      }
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

  private static String preauthorizationJson(String dataConsentJson) {
    return "{\"id\":\"" + PREAUTHORIZATION_ID + "\",\"status\":\"COMPLETED\","
      + "\"consentUrl\":\"https://consent.example.com\","
      + "\"clientPreauthorizationId\":\"client-preauth-1\","
      + "\"callbackUrls\":{\"success\":\"https://example.com/ok\","
      + "\"error\":\"https://example.com/error\"},"
      + "\"recipients\":[" + RECIPIENT_JSON + "],"
      + "\"connector\":{\"id\":612,\"name\":\"Nubank\",\"supportsSmartTransfers\":true},"
      + "\"createdAt\":\"2026-09-01T12:00:00.000Z\",\"updatedAt\":\"2026-09-01T12:05:00.000Z\","
      + "\"configuration\":{\"totalAllowedAmount\":5000,\"transactionLimit\":500.5,"
      + "\"periodicLimits\":{\"day\":{\"quantityLimit\":3,\"transactionLimit\":1000},"
      + "\"month\":{\"quantityLimit\":30}}},"
      + "\"dataConsent\":" + dataConsentJson + "}";
  }

  private JsonObject lastRequestJson() {
    return JsonParser.parseString(lastRequestBody).getAsJsonObject();
  }

  @Test
  void createSmartTransferPreauthorization_withAllFields_sendsBodyAndParsesPreauthorization()
    throws IOException {
    responseJson = preauthorizationJson("{\"status\":\"AWAITING_AUTHORISATION\","
      + "\"rejectionReason\":null,\"updatedAt\":\"2026-09-01T12:00:00.000Z\"}");

    CreateSmartTransferPreauthorizationRequest request = CreateSmartTransferPreauthorizationRequest
      .builder()
      .connectorId(612)
      .parameters(new SmartTransferPreauthorizationParameter("41679949500", "41679495000100"))
      .recipientIds(Collections.singletonList(RECIPIENT_ID))
      .callbackUrls(SmartTransferCallbackUrls.builder()
        .success("https://example.com/ok")
        .error("https://example.com/error")
        .build())
      .clientPreauthorizationId("client-preauth-1")
      .configuration(SmartTransferPreauthorizationConfiguration.builder()
        .totalAllowedAmount(5000.0)
        .transactionLimit(500.5)
        .periodicLimits(SmartTransferPeriodicLimits.builder()
          .day(SmartTransferPeriodicLimit.builder().quantityLimit(3).transactionLimit(1000.0).build())
          .build())
        .build())
      .linkedJourney(true)
      .build();

    Response<SmartTransferPreauthorization> response = client.service()
      .createSmartTransferPreauthorization(request)
      .execute();

    assertEquals("POST", lastRequest.method());
    assertEquals("/smart-transfers/preauthorizations", lastRequest.url().encodedPath());

    JsonObject body = lastRequestJson();
    assertEquals(612, body.get("connectorId").getAsInt());
    assertEquals("41679949500", body.getAsJsonObject("parameters").get("cpf").getAsString());
    assertEquals("41679495000100", body.getAsJsonObject("parameters").get("cnpj").getAsString());
    assertEquals(RECIPIENT_ID, body.getAsJsonArray("recipientIds").get(0).getAsString());
    assertEquals("https://example.com/ok",
      body.getAsJsonObject("callbackUrls").get("success").getAsString());
    assertEquals("https://example.com/error",
      body.getAsJsonObject("callbackUrls").get("error").getAsString());
    assertEquals("client-preauth-1", body.get("clientPreauthorizationId").getAsString());
    JsonObject configuration = body.getAsJsonObject("configuration");
    assertEquals(5000.0, configuration.get("totalAllowedAmount").getAsDouble());
    assertEquals(500.5, configuration.get("transactionLimit").getAsDouble());
    JsonObject day = configuration.getAsJsonObject("periodicLimits").getAsJsonObject("day");
    assertEquals(3, day.get("quantityLimit").getAsInt());
    assertEquals(1000.0, day.get("transactionLimit").getAsDouble());
    // limits that are not set are omitted, not sent as null
    assertFalse(configuration.getAsJsonObject("periodicLimits").has("week"));
    assertTrue(body.get("linkedJourney").getAsBoolean());

    assertTrue(response.isSuccessful());
    SmartTransferPreauthorization preauthorization = response.body();
    assertNotNull(preauthorization);
    assertEquals(PREAUTHORIZATION_ID, preauthorization.getId());
    assertEquals(SmartTransferPreauthorizationStatus.COMPLETED, preauthorization.getStatus());
    assertEquals("https://consent.example.com", preauthorization.getConsentUrl());
    assertEquals("client-preauth-1", preauthorization.getClientPreauthorizationId());
    assertEquals("https://example.com/ok", preauthorization.getCallbackUrls().getSuccess());
    assertEquals("https://example.com/error", preauthorization.getCallbackUrls().getError());
    assertEquals(612, preauthorization.getConnector().getId());
    assertTrue(preauthorization.getConnector().getSupportsSmartTransfers());
    assertEquals(Date.from(Instant.parse("2026-09-01T12:00:00Z")), preauthorization.getCreatedAt());
    assertEquals(Date.from(Instant.parse("2026-09-01T12:05:00Z")), preauthorization.getUpdatedAt());
    assertEquals(5000.0, preauthorization.getConfiguration().getTotalAllowedAmount());
    assertEquals(500.5, preauthorization.getConfiguration().getTransactionLimit());
    SmartTransferPeriodicLimits limits = preauthorization.getConfiguration().getPeriodicLimits();
    assertEquals(3, limits.getDay().getQuantityLimit());
    assertEquals(1000.0, limits.getDay().getTransactionLimit());
    assertEquals(30, limits.getMonth().getQuantityLimit());
    assertNull(limits.getMonth().getTransactionLimit());
    assertNull(limits.getWeek());
    assertNull(preauthorization.getErrorDetail());
    assertEquals(SmartTransferDataConsentStatus.AWAITING_AUTHORISATION,
      preauthorization.getDataConsent().getStatus());

    PaymentRecipient recipient = preauthorization.getRecipients().get(0);
    assertEquals("BANK_ACCOUNT", recipient.getType());
    assertEquals(RECIPIENT_ID, recipient.getId());
    assertEquals("John Doe", recipient.getName());
    assertEquals("11111111111", recipient.getTaxNumber());
    assertFalse(recipient.getIsDefault());
    assertNull(recipient.getPixKey());
    assertEquals(Date.from(Instant.parse("2024-07-31T15:55:47.886Z")), recipient.getCreatedAt());
    assertEquals(Date.from(Instant.parse("2024-07-31T15:56:23.123Z")), recipient.getUpdatedAt());
    assertEquals("Banco XP S.A.", recipient.getPaymentInstitution().getName());
    assertEquals("BCO XP S.A.", recipient.getPaymentInstitution().getTradeName());
    assertEquals("33264668", recipient.getPaymentInstitution().getIspb());
    assertEquals("348", recipient.getPaymentInstitution().getCompe());
    assertNotNull(recipient.getPaymentInstitution().getCreatedAt());
    assertEquals("CHECKING_ACCOUNT", recipient.getAccount().getType());
    assertEquals("1111111", recipient.getAccount().getNumber());
    assertEquals("0001", recipient.getAccount().getBranch());
  }

  @Test
  void createSmartTransferPreauthorization_requiredFieldsOnly_omitsLinkedJourneyAndOptionals()
    throws IOException {
    responseJson = preauthorizationJson("null");

    CreateSmartTransferPreauthorizationRequest request =
      new CreateSmartTransferPreauthorizationRequest(612,
        new SmartTransferPreauthorizationParameter("41679949500"),
        Collections.singletonList(RECIPIENT_ID));

    client.service().createSmartTransferPreauthorization(request).execute();

    JsonObject body = lastRequestJson();
    assertEquals(612, body.get("connectorId").getAsInt());
    assertEquals("41679949500", body.getAsJsonObject("parameters").get("cpf").getAsString());
    assertFalse(body.getAsJsonObject("parameters").has("cnpj"));
    assertEquals(1, body.getAsJsonArray("recipientIds").size());
    assertFalse(body.has("linkedJourney"));
    assertFalse(body.has("callbackUrls"));
    assertFalse(body.has("clientPreauthorizationId"));
    assertFalse(body.has("configuration"));
  }

  @Test
  void createSmartTransferPreauthorization_linkedJourneyFalse_isSent() throws IOException {
    responseJson = preauthorizationJson("null");

    client.service().createSmartTransferPreauthorization(CreateSmartTransferPreauthorizationRequest
      .builder()
      .connectorId(612)
      .parameters(new SmartTransferPreauthorizationParameter("41679949500"))
      .recipientIds(Collections.singletonList(RECIPIENT_ID))
      .linkedJourney(false)
      .build()).execute();

    JsonObject body = lastRequestJson();
    assertTrue(body.has("linkedJourney"));
    assertFalse(body.get("linkedJourney").getAsBoolean());
  }

  @Test
  void getSmartTransferPreauthorizations_withoutFilters_sendsNoQueryAndParsesPage()
    throws IOException {
    responseJson = "{\"page\":1,\"total\":1,\"totalPages\":1,\"results\":["
      + preauthorizationJson("null") + "]}";

    Response<SmartTransferPreauthorizationsResponse> response = client.service()
      .getSmartTransferPreauthorizations()
      .execute();

    assertEquals("GET", lastRequest.method());
    assertEquals("/smart-transfers/preauthorizations", lastRequest.url().encodedPath());
    assertNull(lastRequest.url().query());

    SmartTransferPreauthorizationsResponse page = response.body();
    assertNotNull(page);
    assertEquals(1, page.getPage());
    assertEquals(1, page.getTotal());
    assertEquals(1, page.getTotalPages());
    assertEquals(1, page.getResults().size());
    assertEquals(PREAUTHORIZATION_ID, page.getResults().get(0).getId());
    assertNull(page.getResults().get(0).getDataConsent());
  }

  @Test
  void getSmartTransferPreauthorizations_withPaging_sendsQuery() throws IOException {
    responseJson = "{\"page\":2,\"total\":60,\"totalPages\":2,\"results\":[]}";

    SmartTransferPreauthorizationsResponse page = client.service()
      .getSmartTransferPreauthorizations(
        new SmartTransferPreauthorizationsSearchRequest().page(2).pageSize(50))
      .execute()
      .body();

    assertEquals("/smart-transfers/preauthorizations", lastRequest.url().encodedPath());
    assertEquals("2", lastRequest.url().queryParameter("page"));
    assertEquals("50", lastRequest.url().queryParameter("pageSize"));
    assertNotNull(page);
    assertEquals(2, page.getPage());
    assertTrue(page.getResults().isEmpty());
  }

  @Test
  void getSmartTransferPreauthorization_withDataConsent_parsesIt() throws IOException {
    responseJson = preauthorizationJson("{\"status\":\"AUTHORISED\",\"rejectionReason\":null,"
      + "\"updatedAt\":\"2026-09-01T12:10:00.000Z\"}");

    Response<SmartTransferPreauthorization> response = client.service()
      .getSmartTransferPreauthorization(PREAUTHORIZATION_ID)
      .execute();

    assertEquals("GET", lastRequest.method());
    assertEquals("/smart-transfers/preauthorizations/" + PREAUTHORIZATION_ID,
      lastRequest.url().encodedPath());

    SmartTransferDataConsent dataConsent = response.body().getDataConsent();
    assertNotNull(dataConsent);
    assertEquals(SmartTransferDataConsentStatus.AUTHORISED, dataConsent.getStatus());
    assertNull(dataConsent.getRejectionReason());
    assertEquals(Date.from(Instant.parse("2026-09-01T12:10:00Z")), dataConsent.getUpdatedAt());
  }

  @Test
  void getSmartTransferPreauthorization_rejectedDataConsent_parsesRejectionReason()
    throws IOException {
    responseJson = preauthorizationJson("{\"status\":\"REJECTED\","
      + "\"rejectionReason\":\"CUSTOMER_MANUALLY_REVOKED\","
      + "\"updatedAt\":\"2026-09-02T08:00:00.000Z\"}");

    SmartTransferDataConsent dataConsent = client.service()
      .getSmartTransferPreauthorization(PREAUTHORIZATION_ID)
      .execute()
      .body()
      .getDataConsent();

    assertEquals(SmartTransferDataConsentStatus.REJECTED, dataConsent.getStatus());
    assertEquals("CUSTOMER_MANUALLY_REVOKED", dataConsent.getRejectionReason());
  }

  @Test
  void getSmartTransferPreauthorization_dataConsentNull_parsesAsNull() throws IOException {
    responseJson = preauthorizationJson("null");

    SmartTransferPreauthorization preauthorization = client.service()
      .getSmartTransferPreauthorization(PREAUTHORIZATION_ID)
      .execute()
      .body();

    assertNotNull(preauthorization);
    assertEquals(PREAUTHORIZATION_ID, preauthorization.getId());
    assertNull(preauthorization.getDataConsent());
  }

  @Test
  void getSmartTransferPreauthorization_errorStatus_parsesErrorDetail() throws IOException {
    responseJson = "{\"id\":\"" + PREAUTHORIZATION_ID + "\",\"status\":\"ERROR\","
      + "\"recipients\":[],\"dataConsent\":null,"
      + "\"errorDetail\":{\"code\":\"SOME_CODE\",\"description\":\"Something failed\","
      + "\"detail\":\"More detail\"}}";

    SmartTransferPreauthorization preauthorization = client.service()
      .getSmartTransferPreauthorization(PREAUTHORIZATION_ID)
      .execute()
      .body();

    assertEquals(SmartTransferPreauthorizationStatus.ERROR, preauthorization.getStatus());
    assertEquals("SOME_CODE", preauthorization.getErrorDetail().getCode());
    assertEquals("Something failed", preauthorization.getErrorDetail().getDescription());
    assertEquals("More detail", preauthorization.getErrorDetail().getDetail());
  }

  @Test
  void getSmartTransferPreauthorizationBalance_withOverdraft_parsesIt() throws IOException {
    responseJson = "{\"balance\":5.48,"
      + "\"overdraft\":{\"contracted\":1000,\"used\":250.5,\"available\":749.5}}";

    Response<SmartTransferPreauthorizationBalance> response = client.service()
      .getSmartTransferPreauthorizationBalance(PREAUTHORIZATION_ID)
      .execute();

    assertEquals("GET", lastRequest.method());
    assertEquals("/smart-transfers/preauthorizations/" + PREAUTHORIZATION_ID + "/balance",
      lastRequest.url().encodedPath());

    SmartTransferPreauthorizationBalance balance = response.body();
    assertNotNull(balance);
    assertEquals(5.48, balance.getBalance());
    assertNotNull(balance.getOverdraft());
    assertEquals(1000.0, balance.getOverdraft().getContracted());
    assertEquals(250.5, balance.getOverdraft().getUsed());
    assertEquals(749.5, balance.getOverdraft().getAvailable());
  }

  @Test
  void getSmartTransferPreauthorizationBalance_overdraftNull_parsesAsNull() throws IOException {
    responseJson = "{\"balance\":-12.3,\"overdraft\":null}";

    SmartTransferPreauthorizationBalance balance = client.service()
      .getSmartTransferPreauthorizationBalance(PREAUTHORIZATION_ID)
      .execute()
      .body();

    assertNotNull(balance);
    assertEquals(-12.3, balance.getBalance());
    assertNull(balance.getOverdraft());
  }

  @Test
  void getSmartTransferPreauthorizationBalance_dataConsentNotAvailable_parsesErrorCode()
    throws IOException {
    responseCode = 403;
    responseJson = "{\"code\":403,"
      + "\"codeDescription\":\"SMART_TRANSFER_DATA_CONSENT_NOT_AVAILABLE\","
      + "\"message\":\"The balance permission of this Smart Transfer Preauthorization is not "
      + "available. It may be pending, rejected or revoked.\"}";

    Response<SmartTransferPreauthorizationBalance> response = client.service()
      .getSmartTransferPreauthorizationBalance(PREAUTHORIZATION_ID)
      .execute();

    assertFalse(response.isSuccessful());
    ErrorResponse error = client.parseError(response);
    assertEquals(403, error.getCode());
    assertEquals("SMART_TRANSFER_DATA_CONSENT_NOT_AVAILABLE", error.getCodeDescription());
  }

  @Test
  void cancelSmartTransferPreauthorizationDataConsent_sendsDeleteAndParsesPreauthorization()
    throws IOException {
    responseJson = preauthorizationJson("{\"status\":\"REJECTED\","
      + "\"rejectionReason\":\"CUSTOMER_MANUALLY_REVOKED\","
      + "\"updatedAt\":\"2026-09-03T09:00:00.000Z\"}");

    Response<SmartTransferPreauthorization> response = client.service()
      .cancelSmartTransferPreauthorizationDataConsent(PREAUTHORIZATION_ID)
      .execute();

    assertEquals("DELETE", lastRequest.method());
    assertEquals("/smart-transfers/preauthorizations/" + PREAUTHORIZATION_ID + "/data-consent",
      lastRequest.url().encodedPath());
    assertNull(lastRequestBody);

    SmartTransferPreauthorization preauthorization = response.body();
    assertNotNull(preauthorization);
    // only the balance permission is cancelled: the preauthorization stays active
    assertEquals(SmartTransferPreauthorizationStatus.COMPLETED, preauthorization.getStatus());
    assertEquals(SmartTransferDataConsentStatus.REJECTED,
      preauthorization.getDataConsent().getStatus());
  }

  @Test
  void getSmartTransferPreauthorizationPayments_withoutFilters_sendsNoQueryAndParsesPage()
    throws IOException {
    responseJson = "{\"page\":1,\"total\":1,\"totalPages\":1,\"results\":[{"
      + "\"id\":\"" + PAYMENT_ID + "\",\"preauthorizationId\":\"" + PREAUTHORIZATION_ID + "\","
      + "\"status\":\"PAYMENT_COMPLETED\",\"amount\":100.5,\"description\":\"Rent\","
      + "\"recipient\":" + RECIPIENT_JSON + ",\"clientPaymentId\":\"client-payment-1\","
      + "\"createdAt\":\"2026-09-05T10:00:00.000Z\",\"updatedAt\":\"2026-09-05T10:01:00.000Z\"}]}";

    Response<SmartTransferPaymentsResponse> response = client.service()
      .getSmartTransferPreauthorizationPayments(PREAUTHORIZATION_ID)
      .execute();

    assertEquals("GET", lastRequest.method());
    assertEquals("/smart-transfers/preauthorizations/" + PREAUTHORIZATION_ID + "/payments",
      lastRequest.url().encodedPath());
    assertNull(lastRequest.url().query());

    SmartTransferPaymentsResponse page = response.body();
    assertNotNull(page);
    assertEquals(1, page.getTotal());
    SmartTransferPayment payment = page.getResults().get(0);
    assertEquals(PAYMENT_ID, payment.getId());
    assertEquals(SmartTransferPaymentStatus.PAYMENT_COMPLETED, payment.getStatus());
    assertEquals(100.5, payment.getAmount());
    assertEquals(RECIPIENT_ID, payment.getRecipient().getId());
  }

  @Test
  void getSmartTransferPreauthorizationPayments_withFilters_sendsQuery() throws IOException {
    responseJson = "{\"page\":1,\"total\":0,\"totalPages\":0,\"results\":[]}";

    SmartTransferPreauthorizationPaymentsSearchRequest request =
      new SmartTransferPreauthorizationPaymentsSearchRequest()
        .from("2026-01-01")
        .to("2026-12-31")
        .page(1)
        .pageSize(20);
    client.service().getSmartTransferPreauthorizationPayments(PREAUTHORIZATION_ID, request)
      .execute();

    assertEquals("/smart-transfers/preauthorizations/" + PREAUTHORIZATION_ID + "/payments",
      lastRequest.url().encodedPath());
    assertEquals(4, lastRequest.url().querySize());
    assertEquals("2026-01-01", lastRequest.url().queryParameter("from"));
    assertEquals("2026-12-31", lastRequest.url().queryParameter("to"));
    assertEquals("1", lastRequest.url().queryParameter("page"));
    assertEquals("20", lastRequest.url().queryParameter("pageSize"));
  }

  @Test
  void smartTransferPreauthorizationPaymentsSearchRequest_invalidDate_throws() {
    SmartTransferPreauthorizationPaymentsSearchRequest request =
      new SmartTransferPreauthorizationPaymentsSearchRequest();

    assertThrows(IllegalArgumentException.class, () -> request.from("01/01/2026"));
    assertThrows(IllegalArgumentException.class, () -> request.to("2026-13-01"));
  }

  @Test
  void createSmartTransferPayment_sendsBodyAndParsesPayment() throws IOException {
    responseJson = "{\"id\":\"" + PAYMENT_ID + "\","
      + "\"preauthorizationId\":\"" + PREAUTHORIZATION_ID + "\","
      + "\"status\":\"CONSENT_AUTHORIZED\",\"amount\":100.5,\"description\":\"Rent\","
      + "\"recipient\":" + RECIPIENT_JSON + ",\"clientPaymentId\":\"client-payment-1\","
      + "\"createdAt\":\"2026-09-05T10:00:00.000Z\",\"updatedAt\":\"2026-09-05T10:00:00.000Z\"}";

    CreateSmartTransferPaymentRequest request = CreateSmartTransferPaymentRequest.builder()
      .preauthorizationId(PREAUTHORIZATION_ID)
      .recipientId(RECIPIENT_ID)
      .amount(100.5)
      .description("Rent")
      .clientPaymentId("client-payment-1")
      .build();

    Response<SmartTransferPayment> response = client.service()
      .createSmartTransferPayment(request)
      .execute();

    assertEquals("POST", lastRequest.method());
    assertEquals("/smart-transfers/payments", lastRequest.url().encodedPath());
    JsonObject body = lastRequestJson();
    assertEquals(PREAUTHORIZATION_ID, body.get("preauthorizationId").getAsString());
    assertEquals(RECIPIENT_ID, body.get("recipientId").getAsString());
    assertEquals(100.5, body.get("amount").getAsDouble());
    assertEquals("Rent", body.get("description").getAsString());
    assertEquals("client-payment-1", body.get("clientPaymentId").getAsString());

    SmartTransferPayment payment = response.body();
    assertNotNull(payment);
    assertEquals(PAYMENT_ID, payment.getId());
    assertEquals(PREAUTHORIZATION_ID, payment.getPreauthorizationId());
    assertEquals(SmartTransferPaymentStatus.CONSENT_AUTHORIZED, payment.getStatus());
    assertEquals(100.5, payment.getAmount());
    assertEquals("Rent", payment.getDescription());
    assertEquals("client-payment-1", payment.getClientPaymentId());
    assertEquals("John Doe", payment.getRecipient().getName());
    assertEquals(Date.from(Instant.parse("2026-09-05T10:00:00Z")), payment.getCreatedAt());
    assertEquals(Date.from(Instant.parse("2026-09-05T10:00:00Z")), payment.getUpdatedAt());
    assertNull(payment.getErrorDetail());
  }

  @Test
  void createSmartTransferPayment_requiredFieldsOnly_omitsOptionals() throws IOException {
    responseJson = "{\"id\":\"" + PAYMENT_ID + "\",\"status\":\"PAYMENT_PENDING\"}";

    client.service().createSmartTransferPayment(
      new CreateSmartTransferPaymentRequest(PREAUTHORIZATION_ID, RECIPIENT_ID, 10.0)).execute();

    JsonObject body = lastRequestJson();
    assertEquals(new HashSet<>(Arrays.asList("preauthorizationId", "recipientId", "amount")),
      body.keySet());
    assertEquals(10.0, body.get("amount").getAsDouble());
  }

  @Test
  void getSmartTransferPayment_rejected_parsesErrorDetailAndNullRecipient() throws IOException {
    responseJson = "{\"id\":\"" + PAYMENT_ID + "\","
      + "\"preauthorizationId\":\"" + PREAUTHORIZATION_ID + "\","
      + "\"status\":\"PAYMENT_REJECTED\",\"amount\":100.5,\"recipient\":null,"
      + "\"createdAt\":\"2026-09-05T10:00:00.000Z\",\"updatedAt\":\"2026-09-05T10:02:00.000Z\","
      + "\"errorDetail\":{\"code\":\"PAYMENT_REJECTED_BY_HOLDER\","
      + "\"description\":\"The payment was rejected by the account holder\"}}";

    Response<SmartTransferPayment> response = client.service()
      .getSmartTransferPayment(PAYMENT_ID)
      .execute();

    assertEquals("GET", lastRequest.method());
    assertEquals("/smart-transfers/payments/" + PAYMENT_ID, lastRequest.url().encodedPath());

    SmartTransferPayment payment = response.body();
    assertNotNull(payment);
    assertEquals(SmartTransferPaymentStatus.PAYMENT_REJECTED, payment.getStatus());
    assertNull(payment.getRecipient());
    assertNull(payment.getDescription());
    assertEquals("PAYMENT_REJECTED_BY_HOLDER", payment.getErrorDetail().getCode());
    assertEquals("The payment was rejected by the account holder",
      payment.getErrorDetail().getDescription());
    assertNull(payment.getErrorDetail().getDetail());
  }

  @Test
  void unknownStatuses_deserializeAsNull() throws IOException {
    responseJson = "{\"id\":\"" + PREAUTHORIZATION_ID + "\",\"status\":\"SOME_FUTURE_STATUS\","
      + "\"dataConsent\":{\"status\":\"SOME_FUTURE_STATUS\",\"rejectionReason\":null,"
      + "\"updatedAt\":\"2026-09-01T12:00:00.000Z\"}}";

    SmartTransferPreauthorization preauthorization = client.service()
      .getSmartTransferPreauthorization(PREAUTHORIZATION_ID)
      .execute()
      .body();

    // a status added after this SDK release deserializes as null, like every SDK enum
    assertNotNull(preauthorization);
    assertNull(preauthorization.getStatus());
    assertNull(preauthorization.getDataConsent().getStatus());
  }
}
