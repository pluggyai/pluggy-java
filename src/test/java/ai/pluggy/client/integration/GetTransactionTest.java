package ai.pluggy.client.integration;

import static ai.pluggy.client.integration.helper.TransactionHelper.getFirstAccountTransactions;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import ai.pluggy.client.response.Transaction;
import ai.pluggy.client.response.TransactionsResponse;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import retrofit2.Response;

public class GetTransactionTest extends BaseApiIntegrationTest {

  @Disabled // TODO enable again once it's working...
  @SneakyThrows
  @Test
  void getTransaction_byExistingId_ok() {
    TransactionsResponse firstAccountTransactions = getFirstAccountTransactions(client);
    String existingTransactionId = firstAccountTransactions.getResults().get(0).getId();

    // request existing transaction
    Response<Transaction> transactionResponse = client.service()
      .getTransaction(existingTransactionId)
      .execute();
    
    Transaction transaction = transactionResponse.body();

    // expect retrieved transaction to be not null, and id to match the requested id
    assertNotNull(transaction);
    assertEquals(transaction.getId(), existingTransactionId);
  }
}
