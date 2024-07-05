package ai.pluggy.client.integration;

import static ai.pluggy.client.integration.helper.ItemHelper.createPluggyBankItem;
import static ai.pluggy.client.integration.helper.ItemHelper.getItemStatus;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ai.pluggy.client.integration.util.Poller;
import ai.pluggy.client.request.AccountsRequest;
import ai.pluggy.client.response.AccountsResponse;
import ai.pluggy.client.response.ItemResponse;
import ai.pluggy.client.response.ItemStatus;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import retrofit2.Response;

public class GetAccountsTest extends BaseApiIntegrationTest {

  private ItemResponse pluggyBankExecution;

  @BeforeEach
  void setUp() {
    super.setUp();
    pluggyBankExecution = createPluggyBankItem(client);
    this.getItemsIdCreated().add(pluggyBankExecution.getId());
  }

  @Test
  void getAccounts_immediately_responseEmpty() throws IOException {
    ItemResponse itemCurrentStatus = getItemStatus(client, pluggyBankExecution.getId());
    assertTrue(!Objects.equals(itemCurrentStatus.getStatus(), ItemStatus.UPDATED));

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
    Poller.pollRequestUntil(
        () -> getItemStatus(client, pluggyBankExecution.getId()),
        (ItemResponse itemResponse) -> Objects.equals(itemResponse.getStatus(), ItemStatus.UPDATED),
        500, 30000);

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

    // expect accounts filtered response to have less results than the non-filtered
    // response
    assertTrue(accountsFilteredCount < allAccountsCount, String.format(
        "accountsFilteredCount: %d should be less than allAccountsCount: %d, using filter 'types': '%s'",
        accountsFilteredCount, allAccountsCount, accountTypesFilter));
  }
}
