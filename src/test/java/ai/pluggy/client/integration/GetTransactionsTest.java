package ai.pluggy.client.integration;

import static ai.pluggy.client.integration.helper.AccountHelper.retrieveFirstAccountId;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ai.pluggy.client.request.TransactionsSearchRequest;
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
    String firstAccountId = retrieveFirstAccountId(client, this.getItemsIdCreated());

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
    String firstAccountId = retrieveFirstAccountId(client, this.getItemsIdCreated());

    // precondition: get account transactions (all results)
    Response<TransactionsResponse> allTransactionsResponse = client.service()
      .getTransactions(firstAccountId, new TransactionsSearchRequest().pageSize(100))
      .execute();

    TransactionsResponse allTransactionsResults = allTransactionsResponse.body();

    // expect transactions response to include 3 or more results
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
    String fromDateFilter = "2019-11-15";
    String toDateFilter = middleDate.substring(0, 10);
    TransactionsSearchRequest dateFilters = new TransactionsSearchRequest()
      .from(fromDateFilter)
      .to(toDateFilter)
      .pageSize(100);

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

  @SneakyThrows
  @Test
  void getTransactions_byExistingAccountId_withPageFilters_ok() {
    // precondition: retrieve accounts data
    String firstAccountId = retrieveFirstAccountId(client, this.getItemsIdCreated());

    // fetch first page
    int pageSize = 2;
    int firstPage = 1;
    TransactionsSearchRequest firstPageParams = new TransactionsSearchRequest()
      .page(firstPage)
      .pageSize(pageSize);

    Response<TransactionsResponse> firstPageResponse = client.service()
      .getTransactions(firstAccountId, firstPageParams)
      .execute();

    // precondition: expect to have more txs present in the next page
    TransactionsResponse transactionsFirstPage = firstPageResponse.body();
    assertNotNull(transactionsFirstPage);

    List<Transaction> firstPageTransactions = transactionsFirstPage.getResults();
    int firstPageTxsCount = firstPageTransactions.size();
    Integer allTxsCount = transactionsFirstPage.getTotal();

    // expect to have more pages left
    assertTrue(allTxsCount > firstPageTxsCount,
      String.format("expected total '%d' txs count to be greater than first page txs "
        + "count '%d', for account id '%s'", allTxsCount, firstPageTxsCount, firstAccountId));

    // expect first page to be complete (ie. to equal page size param)
    assertEquals(firstPageTxsCount, pageSize,
      String.format("expected first page txs response count '%d' to equal the page size param '%d'",
        firstPageTxsCount, pageSize));

    // fetch next page
    TransactionsSearchRequest nextPageParams = new TransactionsSearchRequest()
      .page(firstPageParams.getPage() + 1)
      .pageSize(firstPageParams.getPageSize());

    Response<TransactionsResponse> nextPageResponse = client.service()
      .getTransactions(firstAccountId, nextPageParams)
      .execute();

    // expect second page to include results, and to be different than first page.
    TransactionsResponse transactionsNextPage = nextPageResponse.body();
    assertNotNull(transactionsNextPage);
    List<Transaction> nextPageTransactions = transactionsNextPage.getResults();
    assertTrue(nextPageTransactions.size() > 0);

    // expect first tx of next page to be different from the first page.
    assertNotEquals(nextPageTransactions.get(0).getId(),
      firstPageTransactions.get(firstPageTxsCount - 1).getId());
  }

  private String transactionsToIdAndDateStrings(TransactionsResponse allTransactions) {
    return allTransactions.getResults().stream()
      .map(transaction -> String.format(
        "{id=%s, date=%s}", transaction.getId(), transaction.getDate().substring(0, 10)))
      .collect(Collectors.joining(", "));
  }
}
