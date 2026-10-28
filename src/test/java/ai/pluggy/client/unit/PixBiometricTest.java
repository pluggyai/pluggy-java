package ai.pluggy.client.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ai.pluggy.client.PluggyClient;
import ai.pluggy.client.request.ConnectorsSearchRequest;
import ai.pluggy.client.request.CreatePixBiometricPaymentRequest;
import ai.pluggy.client.request.PaymentIntentsSearchRequest;
import ai.pluggy.client.request.PixBiometricEnrollmentsSearchRequest;
import ai.pluggy.client.response.PaymentIntentsResponse;
import ai.pluggy.client.response.PaymentMethod;
import ai.pluggy.client.response.PaymentRequest;
import ai.pluggy.client.response.PaymentRequestCallbackUrls;
import ai.pluggy.client.response.PixBiometricBalance;
import ai.pluggy.client.response.PixBiometricDataConsentStatus;
import ai.pluggy.client.response.PixBiometricEnrollment;
import ai.pluggy.client.response.PixBiometricEnrollmentStatus;
import ai.pluggy.client.response.PixBiometricEnrollmentsResponse;
import ai.pluggy.client.response.PixBiometricRevokedBy;
import ai.pluggy.client.response.WebhookEventPayload;
import ai.pluggy.client.response.WebhookEventType;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.time.Instant;
import java.util.Date;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.ResponseBody;
import okio.Buffer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import retrofit2.Response;

public class PixBiometricTest {

  private static final String ENROLLMENT_ID = "c91a5e2b-7d0f-4f3e-9a51-2f6b8e1d4c77";
  private static final String RECIPIENT_ID = "7b110544-dfcf-406a-bab7-b14f14e2d0c7";

  private static final String ENROLLMENT_JSON = "{\"id\":\"" + ENROLLMENT_ID + "\","
    + "\"status\":\"AUTHORIZED\","
    + "\"connector\":{\"id\":608,\"name\":\"Example Bank\","
    + "\"imageUrl\":\"https://example.com/logo.svg\"},"
    + "\"debtor\":{\"name\":\"John Doe\",\"taxNumber\":\"***.456.789-**\","
    + "\"bankAccount\":{\"agency\":\"1234\",\"account\":\"****4821\",\"name\":\"Example Bank\"}},"
    + "\"cpf\":\"***.456.789-**\",\"cnpj\":null,\"customerId\":null,"
    + "\"limits\":{\"transaction\":1000,\"daily\":3000,\"dailyUsed\":149.9},"
    + "\"expiresAt\":\"2027-10-28T13:05:00.000Z\","
    + "\"dataConsent\":{\"status\":\"AUTHORISED\",\"rejectionReason\":null,"
    + "\"updatedAt\":\"2026-10-28T13:05:00.000Z\"},"
    + "\"revokedBy\":null,\"errorDetail\":null,"
    + "\"createdAt\":\"2026-10-28T13:00:00.000Z\",\"updatedAt\":\"2026-10-28T13:05:00.000Z\"}";

  private PluggyClient client;
  private Request lastRequest;
  private String lastRequestBody;
  private String responseJson;

  @BeforeEach
  void setUp() {
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
        .code(200)
        .message("OK")
        .body(ResponseBody.create(responseJson, MediaType.parse("application/json")))
        .build();
    });
    client = builder.build();
  }

  @Test
  void createPaymentRequestPixBiometric_sendsBodyAndParsesPaymentRequest() throws IOException {
    responseJson = "{\"id\":\"pr-1\",\"amount\":0.01,\"status\":\"CREATED\","
      + "\"paymentUrl\":\"https://pay.example.com/pr-1\",\"isSandbox\":false,"
      + "\"pixBiometric\":{\"linkedJourney\":true,\"enrollment\":null},"
      + "\"createdAt\":\"2026-10-28T13:00:00.000Z\",\"updatedAt\":\"2026-10-28T13:00:00.000Z\"}";

    CreatePixBiometricPaymentRequest request = CreatePixBiometricPaymentRequest.builder()
      .amount(0.01)
      .recipientId(RECIPIENT_ID)
      .description("Order 1")
      .customerId("cus-1")
      .clientPaymentId("order-1")
      .callbackUrls(PaymentRequestCallbackUrls.builder()
        .success("https://example.com/ok").pending("https://example.com/wait")
        .error("https://example.com/error").build())
      .linkedJourney(true)
      .build();
    Response<PaymentRequest> response = client.service()
      .createPaymentRequestPixBiometric(request).execute();

    assertEquals("POST", lastRequest.method());
    assertEquals("/payments/requests/pix-biometric", lastRequest.url().encodedPath());
    JsonObject body = JsonParser.parseString(lastRequestBody).getAsJsonObject();
    assertEquals(0.01, body.get("amount").getAsDouble());
    assertEquals(RECIPIENT_ID, body.get("recipientId").getAsString());
    assertEquals("Order 1", body.get("description").getAsString());
    assertEquals("cus-1", body.get("customerId").getAsString());
    assertEquals("order-1", body.get("clientPaymentId").getAsString());
    assertEquals("https://example.com/wait",
      body.getAsJsonObject("callbackUrls").get("pending").getAsString());
    assertTrue(body.get("linkedJourney").getAsBoolean());
    // Pix por biometria has no sandbox
    assertFalse(body.has("isSandbox"));

    PaymentRequest paymentRequest = response.body();
    assertNotNull(paymentRequest);
    assertEquals("pr-1", paymentRequest.getId());
    assertEquals("https://pay.example.com/pr-1", paymentRequest.getPaymentUrl());
    assertTrue(paymentRequest.getPixBiometric().getLinkedJourney());
    assertNull(paymentRequest.getPixBiometric().getEnrollment());
  }

  @Test
  void createPaymentRequestPixBiometric_minimalConstructor_omitsOptionalFields()
    throws IOException {
    responseJson = "{\"id\":\"pr-1\"}";

    client.service()
      .createPaymentRequestPixBiometric(new CreatePixBiometricPaymentRequest(1.5, RECIPIENT_ID))
      .execute();

    JsonObject body = JsonParser.parseString(lastRequestBody).getAsJsonObject();
    assertEquals(2, body.size());
    assertEquals(1.5, body.get("amount").getAsDouble());
    assertEquals(RECIPIENT_ID, body.get("recipientId").getAsString());
  }

  @Test
  void paymentRequest_paidWithEnrollment_parsesTheEmbeddedSummary() {
    String json = "{\"id\":\"pr-1\",\"pixBiometric\":{\"linkedJourney\":false,"
      + "\"enrollment\":{\"id\":\"" + ENROLLMENT_ID + "\",\"status\":\"AUTHORIZED\","
      + "\"connector\":{\"id\":608,\"name\":\"Example Bank\",\"imageUrl\":\"u\"},"
      + "\"debtor\":null}}}";

    PaymentRequest paymentRequest = new Gson().fromJson(json, PaymentRequest.class);

    assertEquals(ENROLLMENT_ID, paymentRequest.getPixBiometric().getEnrollment().getId());
    assertEquals(PixBiometricEnrollmentStatus.AUTHORIZED,
      paymentRequest.getPixBiometric().getEnrollment().getStatus());
    assertEquals(608, paymentRequest.getPixBiometric().getEnrollment().getConnector().getId());
    assertNull(paymentRequest.getPixBiometric().getEnrollment().getDebtor());
  }

  @Test
  void paymentRequest_ofAnotherKind_hasNoPixBiometric() {
    PaymentRequest absent = new Gson().fromJson("{\"id\":\"pr-1\"}", PaymentRequest.class);
    PaymentRequest nulled = new Gson()
      .fromJson("{\"id\":\"pr-1\",\"pixBiometric\":null}", PaymentRequest.class);

    assertNull(absent.getPixBiometric());
    assertNull(nulled.getPixBiometric());
  }

  @Test
  void getPixBiometricEnrollments_sendsFiltersAsQuery() throws IOException {
    responseJson = "{\"total\":1,\"totalPages\":1,\"page\":1,\"results\":[" + ENROLLMENT_JSON + "]}";

    Response<PixBiometricEnrollmentsResponse> response = client.service()
      .getPixBiometricEnrollments(new PixBiometricEnrollmentsSearchRequest()
        .customerId("cus-1")
        .cpf("12345678900")
        .cnpj("12345678000100")
        .status(PixBiometricEnrollmentStatus.AUTHORIZED)
        .page(2)
        .pageSize(50))
      .execute();

    assertEquals("GET", lastRequest.method());
    assertEquals("/pix-biometric/enrollments", lastRequest.url().encodedPath());
    assertEquals("cus-1", lastRequest.url().queryParameter("customerId"));
    assertEquals("12345678900", lastRequest.url().queryParameter("cpf"));
    assertEquals("12345678000100", lastRequest.url().queryParameter("cnpj"));
    assertEquals("AUTHORIZED", lastRequest.url().queryParameter("status"));
    assertEquals("2", lastRequest.url().queryParameter("page"));
    assertEquals("50", lastRequest.url().queryParameter("pageSize"));

    PixBiometricEnrollmentsResponse page = response.body();
    assertNotNull(page);
    assertEquals(1, page.getTotal());
    assertEquals(ENROLLMENT_ID, page.getResults().get(0).getId());
  }

  @Test
  void getPixBiometricEnrollments_withoutFilters_sendsNoQuery() throws IOException {
    responseJson = "{\"total\":0,\"totalPages\":0,\"page\":1,\"results\":[]}";

    client.service().getPixBiometricEnrollments().execute();

    assertEquals("/pix-biometric/enrollments", lastRequest.url().encodedPath());
    assertNull(lastRequest.url().query());
  }

  @Test
  void getPixBiometricEnrollment_parsesEveryField() throws IOException {
    responseJson = ENROLLMENT_JSON;

    PixBiometricEnrollment enrollment = client.service()
      .getPixBiometricEnrollment(ENROLLMENT_ID).execute().body();

    assertEquals("GET", lastRequest.method());
    assertEquals("/pix-biometric/enrollments/" + ENROLLMENT_ID, lastRequest.url().encodedPath());
    assertNotNull(enrollment);
    assertEquals(PixBiometricEnrollmentStatus.AUTHORIZED, enrollment.getStatus());
    assertEquals(608, enrollment.getConnector().getId());
    assertEquals("Example Bank", enrollment.getConnector().getName());
    assertEquals("***.456.789-**", enrollment.getDebtor().getTaxNumber());
    assertEquals("****4821", enrollment.getDebtor().getBankAccount().getAccount());
    assertEquals("***.456.789-**", enrollment.getCpf());
    assertNull(enrollment.getCnpj());
    assertNull(enrollment.getCustomerId());
    assertEquals(1000.0, enrollment.getLimits().getTransaction());
    assertEquals(3000.0, enrollment.getLimits().getDaily());
    assertEquals(149.9, enrollment.getLimits().getDailyUsed());
    assertEquals(Date.from(Instant.parse("2027-10-28T13:05:00Z")), enrollment.getExpiresAt());
    assertEquals(PixBiometricDataConsentStatus.AUTHORISED, enrollment.getDataConsent().getStatus());
    assertNull(enrollment.getDataConsent().getRejectionReason());
    assertEquals(Date.from(Instant.parse("2026-10-28T13:05:00Z")),
      enrollment.getDataConsent().getUpdatedAt());
    assertNull(enrollment.getRevokedBy());
    assertNull(enrollment.getErrorDetail());
    assertEquals(Date.from(Instant.parse("2026-10-28T13:00:00Z")), enrollment.getCreatedAt());
    assertEquals(Date.from(Instant.parse("2026-10-28T13:05:00Z")), enrollment.getUpdatedAt());
  }

  @Test
  void getPixBiometricEnrollment_unreportedFields_parseAsNull() throws IOException {
    responseJson = "{\"id\":\"" + ENROLLMENT_ID + "\",\"status\":\"CREATED\",\"connector\":null,"
      + "\"debtor\":null,\"limits\":null,\"expiresAt\":null,\"dataConsent\":null}";

    PixBiometricEnrollment enrollment = client.service()
      .getPixBiometricEnrollment(ENROLLMENT_ID).execute().body();

    assertEquals(PixBiometricEnrollmentStatus.CREATED, enrollment.getStatus());
    assertNull(enrollment.getConnector());
    assertNull(enrollment.getDebtor());
    assertNull(enrollment.getLimits());
    assertNull(enrollment.getExpiresAt());
    assertNull(enrollment.getDataConsent());
  }

  @Test
  void getPixBiometricEnrollment_rejectedWithErrorDetail_parsesIt() throws IOException {
    responseJson = "{\"id\":\"" + ENROLLMENT_ID + "\",\"status\":\"REJECTED\","
      + "\"dataConsent\":{\"status\":\"REJECTED\",\"rejectionReason\":\"CUSTOMER_MANUALLY_REJECTED\","
      + "\"updatedAt\":\"2026-10-28T13:05:00.000Z\"},"
      + "\"errorDetail\":{\"code\":\"REJECTED\",\"description\":\"Rejected by the payer\"}}";

    PixBiometricEnrollment enrollment = client.service()
      .getPixBiometricEnrollment(ENROLLMENT_ID).execute().body();

    assertEquals(PixBiometricEnrollmentStatus.REJECTED, enrollment.getStatus());
    assertEquals(PixBiometricDataConsentStatus.REJECTED, enrollment.getDataConsent().getStatus());
    assertEquals("CUSTOMER_MANUALLY_REJECTED", enrollment.getDataConsent().getRejectionReason());
    assertEquals("REJECTED", enrollment.getErrorDetail().getCode());
    assertEquals("Rejected by the payer", enrollment.getErrorDetail().getDescription());
  }

  @Test
  void getPixBiometricEnrollmentBalance_withOverdraft_parsesIt() throws IOException {
    responseJson = "{\"balance\":5.48,"
      + "\"overdraft\":{\"contracted\":1000,\"used\":0,\"available\":1000}}";

    PixBiometricBalance balance = client.service()
      .getPixBiometricEnrollmentBalance(ENROLLMENT_ID).execute().body();

    assertEquals("GET", lastRequest.method());
    assertEquals("/pix-biometric/enrollments/" + ENROLLMENT_ID + "/balance",
      lastRequest.url().encodedPath());
    assertEquals(5.48, balance.getBalance());
    assertEquals(1000.0, balance.getOverdraft().getContracted());
    assertEquals(0.0, balance.getOverdraft().getUsed());
    assertEquals(1000.0, balance.getOverdraft().getAvailable());
  }

  @Test
  void getPixBiometricEnrollmentBalance_overdraftNull_parsesAsNull() throws IOException {
    responseJson = "{\"balance\":-12.3,\"overdraft\":null}";

    PixBiometricBalance balance = client.service()
      .getPixBiometricEnrollmentBalance(ENROLLMENT_ID).execute().body();

    assertEquals(-12.3, balance.getBalance());
    assertNull(balance.getOverdraft());
  }

  @Test
  void revokePixBiometricEnrollment_postsWithoutBody() throws IOException {
    responseJson = "{\"id\":\"" + ENROLLMENT_ID + "\",\"status\":\"REVOKED\","
      + "\"revokedBy\":\"CLIENT\"}";

    PixBiometricEnrollment enrollment = client.service()
      .revokePixBiometricEnrollment(ENROLLMENT_ID).execute().body();

    assertEquals("POST", lastRequest.method());
    assertEquals("/pix-biometric/enrollments/" + ENROLLMENT_ID + "/revoke",
      lastRequest.url().encodedPath());
    // no payload: Retrofit sends an empty body
    assertEquals(0, lastRequest.body().contentLength());
    assertEquals(PixBiometricEnrollmentStatus.REVOKED, enrollment.getStatus());
    assertEquals(PixBiometricRevokedBy.CLIENT, enrollment.getRevokedBy());
  }

  @Test
  void pixBiometricEnrollment_revokedByBankOrPayer_parsesBoth() {
    assertEquals(PixBiometricRevokedBy.BANK, new Gson()
      .fromJson("{\"revokedBy\":\"BANK\"}", PixBiometricEnrollment.class).getRevokedBy());
    assertEquals(PixBiometricRevokedBy.PAYER, new Gson()
      .fromJson("{\"revokedBy\":\"PAYER\"}", PixBiometricEnrollment.class).getRevokedBy());
  }

  @Test
  void cancelPixBiometricEnrollmentDataConsent_sendsDeleteAndParsesEnrollment()
    throws IOException {
    responseJson = "{\"id\":\"" + ENROLLMENT_ID + "\",\"status\":\"AUTHORIZED\","
      + "\"dataConsent\":{\"status\":\"REVOKED\",\"rejectionReason\":null,"
      + "\"updatedAt\":\"2026-10-28T14:00:00.000Z\"}}";

    PixBiometricEnrollment enrollment = client.service()
      .cancelPixBiometricEnrollmentDataConsent(ENROLLMENT_ID).execute().body();

    assertEquals("DELETE", lastRequest.method());
    assertEquals("/pix-biometric/enrollments/" + ENROLLMENT_ID + "/data-consent",
      lastRequest.url().encodedPath());
    assertEquals(PixBiometricEnrollmentStatus.AUTHORIZED, enrollment.getStatus());
    assertEquals(PixBiometricDataConsentStatus.REVOKED, enrollment.getDataConsent().getStatus());
  }

  @Test
  void getPaymentIntents_filtersByEnrollmentAndParsesPaymentMethod() throws IOException {
    responseJson = "{\"total\":1,\"totalPages\":1,\"page\":1,\"results\":[{\"id\":\"pi-1\","
      + "\"status\":\"COMPLETED\",\"paymentMethod\":\"PIX_BIOMETRIC\","
      + "\"enrollmentId\":\"" + ENROLLMENT_ID + "\","
      + "\"createdAt\":\"2026-10-28T13:00:00.000Z\",\"updatedAt\":\"2026-10-28T13:01:00.000Z\"},"
      + "{\"id\":\"pi-2\",\"status\":\"COMPLETED\",\"paymentMethod\":\"PIS\","
      + "\"enrollmentId\":null}]}";

    PaymentIntentsResponse page = client.service()
      .getPaymentIntents(new PaymentIntentsSearchRequest().enrollmentId(ENROLLMENT_ID))
      .execute().body();

    assertEquals("/payments/intents", lastRequest.url().encodedPath());
    assertEquals(ENROLLMENT_ID, lastRequest.url().queryParameter("enrollmentId"));
    assertNull(lastRequest.url().queryParameter("paymentRequestId"));
    assertEquals(PaymentMethod.PIX_BIOMETRIC, page.getResults().get(0).getPaymentMethod());
    assertEquals(ENROLLMENT_ID, page.getResults().get(0).getEnrollmentId());
    assertEquals(PaymentMethod.PIS, page.getResults().get(1).getPaymentMethod());
    assertNull(page.getResults().get(1).getEnrollmentId());
  }

  @Test
  void getPaymentIntents_withoutFilters_sendsNoQuery() throws IOException {
    responseJson = "{\"total\":0,\"totalPages\":0,\"page\":1,\"results\":[]}";

    client.service().getPaymentIntents().execute();

    assertEquals("/payments/intents", lastRequest.url().encodedPath());
    assertNull(lastRequest.url().query());
  }

  @Test
  void connectorsSearch_supportsPixBiometric_isSentAsQuery() throws IOException {
    responseJson = "{\"total\":1,\"totalPages\":1,\"page\":1,\"results\":["
      + "{\"id\":608,\"name\":\"Example Bank\",\"supportsPixBiometric\":true},"
      + "{\"id\":609,\"name\":\"Other Bank\"}]}";

    ai.pluggy.client.response.ConnectorsResponse connectors = client.service()
      .getConnectors(new ConnectorsSearchRequest().setSupportsPixBiometric(true))
      .execute().body();

    assertEquals("true", lastRequest.url().queryParameter("supportsPixBiometric"));
    assertTrue(connectors.getResults().get(0).getSupportsPixBiometric());
    // connectors that predate the flag keep deserializing
    assertNull(connectors.getResults().get(1).getSupportsPixBiometric());
  }

  @Test
  void webhookEventPayload_enrollmentEvent_parsesIdentifiers() {
    String json = "{\"event\":\"pix_biometric_enrollment/authorized\",\"eventId\":\"evt-1\","
      + "\"enrollmentId\":\"" + ENROLLMENT_ID + "\",\"customerId\":null,"
      + "\"paymentRequestId\":\"pr-1\"}";

    WebhookEventPayload payload = new Gson().fromJson(json, WebhookEventPayload.class);

    assertEquals(WebhookEventType.PIX_BIOMETRIC_ENROLLMENT_AUTHORIZED, payload.getEvent());
    assertEquals("evt-1", payload.getEventId());
    assertEquals(ENROLLMENT_ID, payload.getEnrollmentId());
    assertNull(payload.getCustomerId());
    assertEquals("pr-1", payload.getPaymentRequestId());
    assertNull(payload.getItemId());
  }

  @Test
  void webhookEventType_enrollmentRevokedAndError_parse() {
    assertEquals(WebhookEventType.PIX_BIOMETRIC_ENROLLMENT_REVOKED, new Gson()
      .fromJson("{\"event\":\"pix_biometric_enrollment/revoked\"}", WebhookEventPayload.class)
      .getEvent());
    assertEquals(WebhookEventType.PIX_BIOMETRIC_ENROLLMENT_ERROR, new Gson()
      .fromJson("{\"event\":\"pix_biometric_enrollment/error\"}", WebhookEventPayload.class)
      .getEvent());
  }
}
