package ai.pluggy.client.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ai.pluggy.client.PluggyClient;
import ai.pluggy.client.response.Account;
import ai.pluggy.client.response.Bill;
import ai.pluggy.client.response.BillPayment;
import ai.pluggy.client.response.Connector;
import ai.pluggy.client.response.CreditData;
import ai.pluggy.client.response.IdentityFinancialRelationships;
import ai.pluggy.client.response.IdentityProcurator;
import ai.pluggy.client.response.IdentityQualifications;
import ai.pluggy.client.response.IdentityResponse;
import ai.pluggy.client.response.Investment;
import ai.pluggy.client.response.ItemResponse;
import ai.pluggy.client.response.Transaction;
import java.io.IOException;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Deserialization of response fields that map 1:1 to the API's JSON keys (Gson IDENTITY naming), so
 * a casing mismatch would surface here as a null.
 */
public class ResponseFieldsTest {

  private PluggyClient client;
  private String responseJson;

  @BeforeEach
  void setUp() {
    PluggyClient.PluggyClientBuilder builder = PluggyClient.builder()
      .clientIdAndSecret("client-id", "client-secret")
      .noAuthInterceptor();
    // answer every request with a canned body, so no request leaves the process
    builder.okHttpClientBuilder().addInterceptor(chain -> new okhttp3.Response.Builder()
      .request(chain.request())
      .protocol(Protocol.HTTP_1_1)
      .code(200)
      .message("OK")
      .body(ResponseBody.create(responseJson, MediaType.parse("application/json")))
      .build());
    client = builder.build();
  }

  @Test
  void item_parsesConsentAutoSyncAndUserAction() throws IOException {
    responseJson = "{\"id\":\"item-1\",\"consentExpiresAt\":\"2027-01-01T00:00:00.000Z\","
      + "\"nextAutoSyncAt\":\"2026-10-06T08:00:00.000Z\","
      + "\"userAction\":{\"instructions\":\"Scan the QR code\",\"type\":\"qr\","
      + "\"expiresAt\":\"2026-10-05T12:00:00.000Z\",\"attributes\":{\"qr\":\"data:image/png\"}}}";

    ItemResponse item = client.service().getItem("item-1").execute().body();

    assertNotNull(item);
    assertNotNull(item.getConsentExpiresAt());
    assertNotNull(item.getNextAutoSyncAt());
    assertEquals("Scan the QR code", item.getUserAction().getInstructions());
    assertEquals("qr", item.getUserAction().getType());
    assertNotNull(item.getUserAction().getExpiresAt());
    assertEquals("data:image/png", item.getUserAction().getAttributes().get("qr"));
  }

  @Test
  void item_withoutAutoSync_hasNullNextAutoSyncAt() throws IOException {
    responseJson = "{\"id\":\"item-1\",\"nextAutoSyncAt\":null}";

    ItemResponse item = client.service().getItem("item-1").execute().body();

    assertNotNull(item);
    assertNull(item.getNextAutoSyncAt());
    assertNull(item.getUserAction());
  }

  @Test
  void creditData_parsesAdditionalCardsAndLimitFlexibility() throws IOException {
    responseJson = "{\"id\":\"acc-1\",\"creditData\":{\"level\":\"BLACK\","
      + "\"additionalCards\":[{\"number\":\"1234\"},{\"number\":\"5678\"}],"
      + "\"isLimitFlexible\":true}}";

    Account account = client.service().getAccount("acc-1").execute().body();

    assertNotNull(account);
    CreditData creditData = account.getCreditData();
    assertEquals(2, creditData.getAdditionalCards().size());
    assertEquals("5678", creditData.getAdditionalCards().get(1).getNumber());
    assertTrue(creditData.getIsLimitFlexible());
  }

  @Test
  void transaction_parsesOrderAndPaymentDataCodes() throws IOException {
    responseJson = "{\"id\":\"tx-1\",\"order\":3,\"paymentData\":{\"paymentMethod\":\"PIX\","
      + "\"authenticationCode\":\"E1234567820261005\",\"receiverReferenceId\":\"ref-42\"}}";

    Transaction transaction = client.service().getTransaction("tx-1").execute().body();

    assertNotNull(transaction);
    assertEquals(3, transaction.getOrder());
    assertEquals("E1234567820261005", transaction.getPaymentData().getAuthenticationCode());
    assertEquals("ref-42", transaction.getPaymentData().getReceiverReferenceId());
  }

  @Test
  void connector_parsesProductCoverageAndAutomaticPix() throws IOException {
    responseJson = "{\"id\":601,\"name\":\"Bank\",\"supportsAutomaticPix\":true,"
      + "\"productCoverage\":[\"INVESTMENTS:TREASURE_TITLES\",\"CREDIT_OPERATIONS:INVOICE_FINANCINGS\"]}";

    Connector connector = client.service().getConnector(601).execute().body();

    assertNotNull(connector);
    assertTrue(connector.getSupportsAutomaticPix());
    assertEquals(2, connector.getProductCoverage().size());
    assertEquals("INVESTMENTS:TREASURE_TITLES", connector.getProductCoverage().get(0));
  }

  @Test
  void investment_parsesNewFieldsAndNestedObjects() throws IOException {
    responseJson = "{\"id\":\"inv-1\",\"amountProfit\":123.45,\"priceFactor\":1000,"
      + "\"taxExempt\":true,\"gracePeriodDate\":\"2027-03-01T00:00:00.000Z\","
      + "\"couponPayment\":{\"hasCoupon\":true,\"periodicity\":\"IRREGULAR\","
      + "\"additionalInfo\":\"See prospectus\"},"
      + "\"debtor\":{\"name\":\"Agro Company SA\"}}";

    Investment investment = client.service().getInvestment("inv-1").execute().body();

    assertNotNull(investment);
    assertEquals(123.45, Double.valueOf(investment.getAmountProfit()));
    assertEquals(1000.0, investment.getPriceFactor());
    assertTrue(investment.getTaxExempt());
    assertNotNull(investment.getGracePeriodDate());
    assertTrue(investment.getCouponPayment().getHasCoupon());
    assertEquals("IRREGULAR", investment.getCouponPayment().getPeriodicity());
    assertEquals("See prospectus", investment.getCouponPayment().getAdditionalInfo());
    assertEquals("Agro Company SA", investment.getDebtor().getName());
  }

  @Test
  void investment_withNullNestedObjects() throws IOException {
    responseJson = "{\"id\":\"inv-1\",\"amountProfit\":null,\"couponPayment\":null,\"debtor\":null}";

    Investment investment = client.service().getInvestment("inv-1").execute().body();

    assertNotNull(investment);
    assertNull(investment.getAmountProfit());
    assertNull(investment.getCouponPayment());
    assertNull(investment.getDebtor());
  }

  @Test
  void identity_parsesEstablishmentRelationsAndNestedObjects() throws IOException {
    responseJson = "{\"id\":\"id-1\",\"establishmentCode\":\"EST-1\",\"establishmentName\":\"Store\","
      + "\"relations\":[{\"type\":\"Mother\",\"name\":\"Maria Silva\",\"document\":\"00000000000\"}],"
      + "\"financialRelationships\":{\"startDate\":\"2015-01-01T00:00:00.000Z\","
      + "\"productsServicesType\":[\"CONTA_DEPOSITO_A_VISTA\",\"OUTROS\"],"
      + "\"productsServicesTypeAdditionalInfo\":\"Other product\","
      + "\"procurators\":[{\"type\":\"PROCURADOR\",\"cpfNumber\":\"11111111111\","
      + "\"documentNumber\":\"11111111111\",\"documentType\":\"CPF\",\"civilName\":\"Joao\","
      + "\"socialName\":\"Jo\"}],"
      + "\"accounts\":[{\"compeCode\":\"001\",\"branchCode\":\"1234\",\"number\":\"56789\","
      + "\"checkDigit\":\"0\",\"type\":\"CONTA_DEPOSITO_A_VISTA\",\"subtype\":\"INDIVIDUAL\"}],"
      + "\"portabilitiesReceived\":[{\"employerName\":\"Acme\",\"employerDocument\":\"22222222000100\","
      + "\"paycheckBankDetainerCnpj\":\"33333333000100\",\"paycheckBankDetainerIspb\":\"12345678\","
      + "\"portabilityApprovalDate\":\"2024-05-01T00:00:00.000Z\"}],"
      + "\"paychecksBankLink\":[{\"employerName\":\"Acme\",\"employerDocument\":\"22222222000100\","
      + "\"paycheckBankCnpj\":\"44444444000100\",\"paycheckBankIspb\":\"87654321\","
      + "\"accountOpeningDate\":\"2020-02-01T00:00:00.000Z\"}]},"
      + "\"qualifications\":{\"companyCnpj\":\"55555555000100\",\"occupationCode\":\"OUTRO\","
      + "\"occupationDescription\":\"Engineer\","
      + "\"informedIncome\":{\"frequency\":\"MENSAL\",\"amount\":10000.5,"
      + "\"date\":\"2026-01-01T00:00:00.000Z\"},"
      + "\"informedPatrimony\":{\"amount\":500000,\"year\":2025,"
      + "\"date\":\"2025-12-31T00:00:00.000Z\"},"
      + "\"economicActivities\":[{\"code\":\"0111301\",\"isMain\":true},"
      + "{\"code\":\"4711302\",\"isMain\":false}],"
      + "\"informedRevenue\":{\"amount\":1200000,\"frequency\":\"OTHER\","
      + "\"frequencyAdditionalInfo\":\"Seasonal\",\"year\":2025}}}";

    IdentityResponse identity = client.service().getIdentityById("id-1").execute().body();

    assertNotNull(identity);
    assertEquals("EST-1", identity.getEstablishmentCode());
    assertEquals("Store", identity.getEstablishmentName());
    assertEquals("Mother", identity.getRelations().get(0).getType());
    assertEquals("Maria Silva", identity.getRelations().get(0).getName());

    IdentityFinancialRelationships fr = identity.getFinancialRelationships();
    assertNotNull(fr.getStartDate());
    assertEquals(2, fr.getProductsServicesType().size());
    assertEquals("Other product", fr.getProductsServicesTypeAdditionalInfo());
    IdentityProcurator procurator = fr.getProcurators().get(0);
    assertEquals("PROCURADOR", procurator.getType());
    assertEquals("11111111111", procurator.getDocumentNumber());
    assertEquals("CPF", procurator.getDocumentType());
    assertEquals("Joao", procurator.getCivilName());
    assertEquals("Jo", procurator.getSocialName());
    assertEquals("001", fr.getAccounts().get(0).getCompeCode());
    assertEquals("0", fr.getAccounts().get(0).getCheckDigit());
    assertEquals("INDIVIDUAL", fr.getAccounts().get(0).getSubtype());
    assertEquals("12345678", fr.getPortabilitiesReceived().get(0).getPaycheckBankDetainerIspb());
    assertNotNull(fr.getPortabilitiesReceived().get(0).getPortabilityApprovalDate());
    assertEquals("87654321", fr.getPaychecksBankLink().get(0).getPaycheckBankIspb());
    assertNotNull(fr.getPaychecksBankLink().get(0).getAccountOpeningDate());

    IdentityQualifications q = identity.getQualifications();
    assertEquals("55555555000100", q.getCompanyCnpj());
    assertEquals("OUTRO", q.getOccupationCode());
    assertEquals("Engineer", q.getOccupationDescription());
    assertEquals("MENSAL", q.getInformedIncome().getFrequency());
    assertEquals(10000.5, q.getInformedIncome().getAmount());
    assertNotNull(q.getInformedIncome().getDate());
    assertEquals(2025, q.getInformedPatrimony().getYear());
    assertEquals(500000.0, q.getInformedPatrimony().getAmount());
    assertEquals(2, q.getEconomicActivities().size());
    assertTrue(q.getEconomicActivities().get(0).getIsMain());
    assertFalse(q.getEconomicActivities().get(1).getIsMain());
    assertEquals("0111301", q.getEconomicActivities().get(0).getCode());
    assertEquals("OTHER", q.getInformedRevenue().getFrequency());
    assertEquals("Seasonal", q.getInformedRevenue().getFrequencyAdditionalInfo());
    assertEquals(2025, q.getInformedRevenue().getYear());
  }

  @Test
  void bill_parsesPayments() throws IOException {
    responseJson = "{\"id\":\"bill-1\",\"payments\":[{\"id\":\"pay-1\",\"valueType\":\"FULL_PAYMENT\","
      + "\"paymentDate\":\"2026-09-10T00:00:00.000Z\",\"paymentMode\":\"PIX\",\"amount\":1500.75,"
      + "\"currencyCode\":\"BRL\"},{\"id\":\"pay-2\",\"valueType\":\"OTHER_PAYMENT\","
      + "\"paymentDate\":\"2026-09-11T00:00:00.000Z\",\"paymentMode\":null,\"amount\":10,"
      + "\"currencyCode\":\"BRL\"}]}";

    Bill bill = client.service().getBill("bill-1").execute().body();

    assertNotNull(bill);
    assertEquals(2, bill.getPayments().size());
    BillPayment payment = bill.getPayments().get(0);
    assertEquals("pay-1", payment.getId());
    assertEquals("FULL_PAYMENT", payment.getValueType());
    assertNotNull(payment.getPaymentDate());
    assertEquals("PIX", payment.getPaymentMode());
    assertEquals(1500.75, payment.getAmount());
    assertEquals("BRL", payment.getCurrencyCode());
    assertNull(bill.getPayments().get(1).getPaymentMode());
  }
}
