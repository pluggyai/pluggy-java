package ai.pluggy.client.integration;

import static ai.pluggy.client.integration.helper.ItemHelper.createPluggyBankItem;
import static ai.pluggy.client.integration.helper.ItemHelper.getItemStatus;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ai.pluggy.client.integration.util.Poller;
import ai.pluggy.client.response.InvestmentsResponse;
import ai.pluggy.client.response.ItemResponse;
import ai.pluggy.client.response.ItemStatus;

import java.util.Objects;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import retrofit2.Response;

public class GetInvestmentsTest extends BaseApiIntegrationTest {

  @SneakyThrows
  @Test
  void getInvestments_afterExecutionCompleted_responseWithResults() {
    // precondition: start pluggy bank execution & wait for completion
    ItemResponse pluggyBankExecution = createPluggyBankItem(client);

    // poll check of connector item status until it's completed (status: "UPDATED")
    Poller.pollRequestUntil(
        () -> getItemStatus(client, pluggyBankExecution.getId()),
        (ItemResponse itemResponse) -> Objects.equals(itemResponse.getStatus(), ItemStatus.UPDATED),
        500, 30000);

    // get investments request
    Response<InvestmentsResponse> investmentsResponse = client.service().getInvestments(pluggyBankExecution.getId())
        .execute();

    // expect investments response to be valid & contain 1 or more results
    assertTrue(investmentsResponse.isSuccessful());
    InvestmentsResponse investments = investmentsResponse.body();
    assertNotNull(investments);
    assertTrue(investments.getResults().size() > 0);

    this.getItemsIdCreated().add(pluggyBankExecution.getId());
  }
}
