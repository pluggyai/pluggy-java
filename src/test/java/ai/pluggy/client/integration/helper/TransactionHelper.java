package ai.pluggy.client.integration.helper;

import static ai.pluggy.client.integration.helper.AccountHelper.retrieveFirstAccountId;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ai.pluggy.client.PluggyClient;
import ai.pluggy.client.response.TransactionsResponse;
import java.io.IOException;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import retrofit2.Response;

@Log4j2
public class TransactionHelper {

  public static TransactionsResponse getFirstAccountTransactions(PluggyClient client, List<String> itemsIdCreated)
    throws IOException, InterruptedException {
    // precondition: retrieve accounts data
    String firstAccountId = retrieveFirstAccountId(client, itemsIdCreated);

    log.info("Retrieving transactions by account id '" + firstAccountId + "' (the first one)");

    // get first account transactions
    Response<TransactionsResponse> transactionsResponse = client.service()
      .getTransactions(firstAccountId)
      .execute();

    TransactionsResponse transactions = transactionsResponse.body();

    // assert transactions result not null and not empty
    assertNotNull(transactions);
    int transactionsCount = transactions.getResults().size();
    assertTrue(transactionsCount > 0);

    log
      .info("Got " + transactionsCount + " transactions for account id '" + firstAccountId + "'. ");
    return transactions;
  }
}
