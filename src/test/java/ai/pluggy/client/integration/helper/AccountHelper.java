package ai.pluggy.client.integration.helper;

import static ai.pluggy.client.integration.helper.ItemHelper.createPluggyBankItem;
import static ai.pluggy.client.integration.helper.ItemHelper.getItemStatus;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ai.pluggy.client.PluggyClient;
import ai.pluggy.client.integration.util.Poller;
import ai.pluggy.client.response.Account;
import ai.pluggy.client.response.AccountsResponse;
import ai.pluggy.client.response.ItemResponse;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.extern.log4j.Log4j2;
import retrofit2.Response;

@Log4j2
public class AccountHelper {

  public static AccountsResponse getPluggyBankAccounts(PluggyClient client, List<String> itemsIdCreated)
    throws InterruptedException, IOException {
    log.info("Retrieving accounts from pluggy bank connector...");
    ItemResponse pluggyBankExecution = createPluggyBankItem(client);

    itemsIdCreated.add(pluggyBankExecution.getId());
    // poll check of connector item status until it's completed (status: "UPDATED")
    Poller.pollRequestUntil(
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

    String accountsIds = accountsResponse.getResults().stream().map(Account::getId)
      .collect(Collectors.joining("' , '", "['", "']"));
    log.info("Accounts retrieved, got {} results (exec item: '{}', account ids: {}).",
      allAccountsCount, pluggyBankExecution.getId(), accountsIds);
    return accountsResponse;
  }

  public static String retrieveFirstAccountId(PluggyClient client, List<String> itemsIdCreated)
    throws InterruptedException, IOException {
    log.info("Retrieving first account id...");
    AccountsResponse pluggyBankAccounts = getPluggyBankAccounts(client, itemsIdCreated);
    String firstAccountId = pluggyBankAccounts.getResults().get(0).getId();
    log.info("Got first account id {}", firstAccountId);
    return firstAccountId;
  }
}
