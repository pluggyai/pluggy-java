package ai.pluggy.client.integration;

import static ai.pluggy.client.integration.helper.AccountHelper.getPluggyBankAccounts;
import static ai.pluggy.client.integration.helper.ItemHelper.NON_EXISTING_ITEM_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ai.pluggy.client.response.Account;
import ai.pluggy.client.response.AccountsResponse;
import ai.pluggy.client.response.ErrorResponse;
import java.io.IOException;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import retrofit2.Response;

@Log4j2
public class GetAccountTest extends BaseApiIntegrationTest {

  @Test
  void getAccount_nonExistingId_responseError404() throws IOException {
    // get account response
    Response<Account> getAccountsResponse = client.service()
      .getAccount(NON_EXISTING_ITEM_ID)
      .execute();

    // expect response to not be succesful
    assertFalse(getAccountsResponse.isSuccessful());

    // expect error response status 404
    ErrorResponse errorResponse = client.parseError(getAccountsResponse);
    assertNotNull(errorResponse);
    assertEquals(errorResponse.getCode(), 404);
  }

  @SneakyThrows
  @Test
  void getAccountById_afterExecutionCompleted_responseOk() {
    // precondition: run sandbox accounts execution, wait for result, and retrieve it
    AccountsResponse pluggyBankAccounts = getPluggyBankAccounts(client, this.getItemsIdCreated());

    // get account response
    String firstAccountId = pluggyBankAccounts.getResults().get(0).getId();
    Response<Account> getAccountResponse = client.service()
      .getAccount(firstAccountId)
      .execute();

    // expect account response to be OK 
    assertTrue(getAccountResponse.isSuccessful());
    Account accountResponse = getAccountResponse.body();
    assertNotNull(accountResponse);

    // expect account id to match the request id
    assertEquals(accountResponse.getId(), firstAccountId);
  }

}
