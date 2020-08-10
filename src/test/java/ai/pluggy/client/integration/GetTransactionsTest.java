package ai.pluggy.client.integration;

import static ai.pluggy.client.integration.helper.AccountHelper.retrieveFirstAccountId;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ai.pluggy.client.request.DateFilters;
import ai.pluggy.client.response.Transaction;
import ai.pluggy.client.response.TransactionsResponse;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
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

    TransactionsResponse allTransactionsResults = allTransactionsResponse.body();

    // expect transactions response to include 1 or more results
    assertNotNull(allTransactionsResults);
    List<Transaction> allTransactions = allTransactionsResults.getResults();
    assertNotNull(allTransactions);

    // build error string in case of no transactions filtered result.
    String allTxsString = transactionsToIdAndDateStrings(allTransactionsResults);

    int allTransactionsCount = allTransactions.size();
    assertTrue(allTransactionsCount >= 3,
      String.format("Expected at least 3 txs for account id '%s', got tx(s): ['%s']",
        firstAccountId, allTxsString));

    // sort transactions result by date string, and extract a sub-range of dates from the total txs results
    allTransactions.sort(Comparator.comparing(Transaction::getDate));
    String minDate = allTransactions.get(0).getDate();
    String middleDate = allTransactions.get(Math.floorDiv(allTransactionsCount, 2)).getDate();
    String maxDate = allTransactions.get(allTransactionsCount - 1).getDate();
    assertTrue(minDate.compareTo(maxDate) < 0,
      String.format("expected min date '%s' of txs to be earlier than max date '%s'",
        minDate, maxDate));
    assertTrue(minDate.compareTo(middleDate) < 0,
      String.format("expected min date '%s' of txs to be earlier than middle date '%s'",
        minDate, middleDate));
    assertTrue(middleDate.compareTo(maxDate) < 0,
      String.format("expected middle date '%s' of txs to be earlier than max date '%s'",
        minDate, middleDate));

    // get account transactions with sub-range date filters
    String fromDateFilter = minDate.substring(0, 10);
    String toDateFilter = middleDate.substring(0, 10);
    DateFilters dateFilters = new DateFilters(fromDateFilter, toDateFilter);
    Response<TransactionsResponse> transactionsFilteredResponse = client.service()
      .getTransactions(firstAccountId, dateFilters)
      .execute();

    TransactionsResponse transactionsFiltered = transactionsFilteredResponse.body();

    // expect filtered transactions response to include 1 or more results
    assertNotNull(transactionsFiltered);
    assertNotNull(transactionsFiltered.getResults());
    int transactionsFilteredCount = transactionsFiltered.getResults().size();

    String expectedTransactionsFilteredMsg = String.format(
      "Expected at least 1 tx between '%s' (out of total '%d' txs) for account id '%s', all txs: '%s'",
      dateFilters,
      allTransactionsCount, firstAccountId, allTxsString);

    assertTrue(transactionsFilteredCount > 0, expectedTransactionsFilteredMsg);

    // expect filtered transactions count to be less than total transactions count
    assertTrue(transactionsFilteredCount < allTransactionsCount,
      String.format(
        "Transactions filtered result: %d should be less than all transactions result: %d, using date filters '%s'",
        transactionsFilteredCount, allTransactionsCount, dateFilters));
  }

  private String transactionsToIdAndDateStrings(TransactionsResponse allTransactions) {
    return allTransactions.getResults().stream()
      .map(transaction -> String.format(
        "{id=%s, date=%s}", transaction.getId(), transaction.getDate().substring(0, 10)))
      .collect(Collectors.joining(", "));
  }
}
