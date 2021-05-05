package ai.pluggy.client.integration;

import static ai.pluggy.client.integration.helper.TransactionHelper.getFirstAccountTransactions;
import static ai.pluggy.client.integration.util.AssertionsUtils.assertSuccessful;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import ai.pluggy.client.response.Transaction;
import ai.pluggy.client.response.TransactionsResponse;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import retrofit2.Response;

public class GetTransactionTest extends BaseApiIntegrationTest {

  @Disabled("currently, GET '/transactions/{id}' is responding with 404 - reenable once it's working again" )
  @SneakyThrows
  @Test
  void getTransaction_byExistingId_ok() {
    TransactionsResponse firstAccountTransactions = getFirstAccountTransactions(client, this.getItemsIdCreated());
    String existingTransactionId = firstAccountTransactions.getResults().get(0).getId();

    // request existing transaction
    Response<Transaction> transactionResponse = client.service()
      .getTransaction(existingTransactionId)
      .execute();
    
    assertSuccessful(transactionResponse, client);
    Transaction transaction = transactionResponse.body();

    // expect retrieved transaction to be not null, and id to match the requested id
    assertNotNull(transaction);
    assertEquals(transaction.getId(), existingTransactionId);
  }
}
