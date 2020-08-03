package ai.pluggy.client.integration;

import static ai.pluggy.client.integration.helper.ItemHelper.PLUGGY_BANK_CONNECTOR_ID;
import static ai.pluggy.client.integration.helper.ItemHelper.createItem;
import static ai.pluggy.client.integration.helper.ItemHelper.getItemStatus;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ai.pluggy.client.request.AccountsRequest;
import ai.pluggy.client.request.ParametersMap;
import ai.pluggy.client.response.AccountsResponse;
import ai.pluggy.client.response.ItemResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.Supplier;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import retrofit2.Response;

@Log4j2
public class GetAccountsTest extends BaseApiIntegrationTest {

  private ItemResponse pluggyBankExecution;

  @BeforeEach
  void setUp() {
    super.setUp();
    // TODO: remove credentials from source code
    ParametersMap parametersMap = ParametersMap
      .map("user", "user-ok")
      .with("password", "password-ok");
    pluggyBankExecution = createItem(client, PLUGGY_BANK_CONNECTOR_ID, parametersMap);
  }

  @Test
  void getAccounts_immediately_responseEmpty() throws IOException {
    ItemResponse itemCurrentStatus = getItemStatus(client, pluggyBankExecution.getId());
    assertTrue(!Objects.equals(itemCurrentStatus.getStatus(), "UPDATED"));

    Response<AccountsResponse> getAccountsResponse = client.service()
      .getAccounts(pluggyBankExecution.getId())
      .execute();

    assertTrue(getAccountsResponse.isSuccessful());

    AccountsResponse accountsResponse = getAccountsResponse.body();
    assertNotNull(accountsResponse);
    assertTrue(accountsResponse.getResults().isEmpty());
  }

  @SneakyThrows
  @Test
  void getAccounts_afterExecutionCompleted_responseWithResults() {
    // poll check of connector item status until it's completed (status: "UPDATED")
    pollRequestUntil(
      () -> getItemStatus(client, pluggyBankExecution.getId()),
      (ItemResponse itemResponse) -> Objects.equals(itemResponse.getStatus(), "UPDATED"),
      500, 30000
    );

    // get accounts response
    Response<AccountsResponse> getAccountsResponse = client.service()
      .getAccounts(pluggyBankExecution.getId())
      .execute();

    // expect accounts response to be OK and have at least 1 result.
    assertTrue(getAccountsResponse.isSuccessful());

    AccountsResponse accountsResponse = getAccountsResponse.body();
    assertNotNull(accountsResponse);
    int allAccountsCount = accountsResponse.getResults().size();
    assertTrue(allAccountsCount > 0);

    // get accounts response - with filter params
    List<String> accountTypesFilter = Arrays.asList("BANK");
    Response<AccountsResponse> getAccountsFilteredResponse = client.service()
      .getAccounts(new AccountsRequest(pluggyBankExecution.getId(), accountTypesFilter))
      .execute();

    // expect accounts response to be OK and have at least 1 result.
    assertTrue(getAccountsFilteredResponse.isSuccessful());

    AccountsResponse accountsFilteredResponse = getAccountsFilteredResponse.body();
    assertNotNull(accountsFilteredResponse);
    int accountsFilteredCount = accountsFilteredResponse.getResults().size();
    assertTrue(accountsFilteredCount > 0);

    // expect accounts filtered response to have less results than the non-filtered response
    assertTrue(accountsFilteredCount < allAccountsCount, String.format(
      "accountsFilteredCount: %d should be less than allAccountsCount: %d, using filter 'types': '%s'",
      accountsFilteredCount, allAccountsCount, accountTypesFilter));
  }

  /**
   * Util method to run a request periodically until specified condition is met, or
   * max timeout is exceeded.
   *
   * @param requestRunner     - entity response supplier, that will be used to test for the condition predicate.
   * @param expectedCondition - predicate that will test each runner result, until it's fullfiled
   * @param pollIntervalMs    - base interval MS. However it's always increased by a 1.5 factor to prevent overloading the server.
   * @param maxTimeoutMs      - maximum timeout to shortcut the execution, in case the condition takes way too long or doesn't resolve at all.
   * @param <T>               - the instance type of the requestRunner response
   */
  private <T> void pollRequestUntil(Supplier<T> requestRunner, Predicate<T> expectedCondition,
    Integer pollIntervalMs, Integer maxTimeoutMs) throws InterruptedException {
    Date startTime = new Date();
    Integer tries = 0;
    log.debug("Polling request, interval ms: " + pollIntervalMs + ", max timeout: " + maxTimeoutMs
      + "ms...");
    T result = requestRunner.get();
    long elapsedTime = 0;
    while (!expectedCondition.test(result)) {
      elapsedTime = new Date().getTime() - startTime.getTime();
      if (elapsedTime > maxTimeoutMs) {
        throw new RuntimeException(
          "Timeout exceeded after " + tries + " tries (" + elapsedTime + "ms)");
      }
      log.debug("Condition not met after attempt #" + tries + ", (elapsed: " + elapsedTime
        + "ms) retrying in " + pollIntervalMs + "ms...");
      pollIntervalMs = (int) Math.floor(pollIntervalMs * 1.5f); // exponential backoff
      Thread.sleep(pollIntervalMs);
      result = requestRunner.get();
      tries++;
    }
    elapsedTime = new Date().getTime() - startTime.getTime();
    log.debug("Condition met after " + tries + " attempts, in " + elapsedTime + "ms");
  }
}
