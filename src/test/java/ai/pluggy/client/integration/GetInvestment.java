package ai.pluggy.client.integration;

import static ai.pluggy.client.integration.helper.InvestmentHelper.getPluggyBankInvestments;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ai.pluggy.client.response.Investment;
import ai.pluggy.client.response.InvestmentsResponse;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import retrofit2.Response;

public class GetInvestment extends BaseApiIntegrationTest {

  @SneakyThrows
  @Test
  void getInvestment_byExistingId_ok() {
    // precondition: get existing investments
    InvestmentsResponse investments = getPluggyBankInvestments(client);
    String firstInvestmentId = investments.getResults().get(0).getId();

    // get investment by existing id
    Response<Investment> investmentResponse = client.service().getInvestment(firstInvestmentId)
      .execute();
    
    // expect investment response to be valid & match the requested id
    assertTrue(investmentResponse.isSuccessful());
    Investment investment = investmentResponse.body();
    assertNotNull(investment);
    assertEquals(investment.getId(), firstInvestmentId);
  }
}
