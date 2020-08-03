package ai.pluggy.client.integration;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ai.pluggy.client.integration.helper.AccountHelper;
import ai.pluggy.client.response.AccountsResponse;
import ai.pluggy.client.response.TransactionsResponse;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import retrofit2.Response;

public class GetTransactionsTest extends BaseApiIntegrationTest {

  @SneakyThrows
  @Test
  void getTransactions_byExistingAccountId_ok() {
    // precondition: retrieve accounts data
    AccountsResponse pluggyBankAccounts = AccountHelper.getPluggyBankAccounts(client);
    String firstAccountId = pluggyBankAccounts.getResults().get(0).getId();

    // get first account transactions
    Response<TransactionsResponse> transactionsResponse = client.service()
      .getTransactions(firstAccountId)
      .execute();

    TransactionsResponse transactions = transactionsResponse.body();

    // expect transactions response to contain 1 or more results
    assertNotNull(transactions);
    assertNotNull(transactions.getResults());
    assertTrue(transactions.getResults().size() > 0);
  }
}
