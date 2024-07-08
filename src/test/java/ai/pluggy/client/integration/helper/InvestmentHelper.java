package ai.pluggy.client.integration.helper;

import static ai.pluggy.client.integration.helper.ItemHelper.createPluggyBankItem;
import static ai.pluggy.client.integration.helper.ItemHelper.getItemStatus;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ai.pluggy.client.PluggyClient;
import ai.pluggy.client.integration.util.Poller;
import ai.pluggy.client.response.InvestmentsResponse;
import ai.pluggy.client.response.ItemResponse;
import ai.pluggy.client.response.ItemStatus;

import java.io.IOException;
import java.util.Objects;
import lombok.extern.log4j.Log4j2;
import retrofit2.Response;

@Log4j2
public class InvestmentHelper {

  public static InvestmentsResponse getPluggyBankInvestments(PluggyClient client)
      throws InterruptedException, IOException {
    log.info("Retrieving investments from pluggy bank connector...");
    ItemResponse pluggyBankExecution = createPluggyBankItem(client);

    // poll check of connector item status until it's completed (status: "UPDATED")
    Poller.pollRequestUntil(
        () -> getItemStatus(client, pluggyBankExecution.getId()),
        (ItemResponse itemResponse) -> Objects.equals(itemResponse.getStatus(), ItemStatus.UPDATED),
        500, 30000);

    // get accounts response
    Response<InvestmentsResponse> getInvestmentsResponse = client.service()
        .getInvestments(pluggyBankExecution.getId())
        .execute();

    // expect accounts response to be OK and have at least 1 result.
    assertTrue(getInvestmentsResponse.isSuccessful());

    InvestmentsResponse investmentsResponse = getInvestmentsResponse.body();
    assertNotNull(investmentsResponse);
    int investmentsCount = investmentsResponse.getResults().size();
    assertTrue(investmentsCount > 0);

    log.info("Investments retrieved, got " + investmentsCount + " results.");
    return investmentsResponse;
  }
}
