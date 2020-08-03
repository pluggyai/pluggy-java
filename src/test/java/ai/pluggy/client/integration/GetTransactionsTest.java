package ai.pluggy.client.integration;

import static ai.pluggy.client.integration.helper.AccountHelper.retrieveFirstAccountId;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ai.pluggy.client.request.DateFilters;
import ai.pluggy.client.response.TransactionsResponse;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import retrofit2.Response;

public class GetTransactionsTest extends BaseApiIntegrationTest {

  @SneakyThrows
  @Test
  void getTransactions_byExistingAccountId_ok() {
    // precondition: retrieve accounts data
    String firstAccountId = retrieveFirstAccountId(client);

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

  @SneakyThrows
  @Test
  void getTransactions_byExistingAccountId_withDateFilters_ok() {
    // precondition: retrieve accounts data
    String firstAccountId = retrieveFirstAccountId(client);

    // precondition: get account transactions (all results)
    Response<TransactionsResponse> allTransactionsResponse = client.service()
      .getTransactions(firstAccountId)
      .execute();

    TransactionsResponse allTransactions = allTransactionsResponse.body();

    // get account transactions with date filters
    DateFilters dateFilters = new DateFilters("2020-05-01", "2020-06-01");
    Response<TransactionsResponse> transactionsFilteredResponse = client.service()
      .getTransactions(firstAccountId, dateFilters)
      .execute();

    TransactionsResponse transactionsFiltered = transactionsFilteredResponse.body();

    // expect transactions response to include 1 or more results
    assertNotNull(allTransactions);
    assertNotNull(allTransactions.getResults());
    int allTransactionsCount = allTransactions.getResults().size();
    assertTrue(allTransactionsCount > 0);

    // expect filtered transactions response to include 1 or more results
    assertNotNull(transactionsFiltered);
    assertNotNull(transactionsFiltered.getResults());
    int transactionsFilteredCount = transactionsFiltered.getResults().size();
    assertTrue(transactionsFilteredCount > 0);

    // expect filtered transactions count to be less than total transactions count
    assertTrue(transactionsFilteredCount < allTransactionsCount,
      String.format(
        "Transations filtered result: %d should be less than all transactions result: %d, using date filters '%s'",
        transactionsFilteredCount, allTransactionsCount, dateFilters));
  }
}
