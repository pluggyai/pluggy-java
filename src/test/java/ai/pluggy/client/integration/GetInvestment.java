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

import java.util.Arrays;

public class GetInvestment extends BaseApiIntegrationTest {

  @SneakyThrows
  @Test
  void getInvestment_byExistingId_ok() {
    // precondition: get existing investments
    InvestmentsResponse investments = getPluggyBankInvestments(client);
    String firstInvestmentId = investments.getResults().get(0).getId();
    String firstInvestmentTransactionId = Arrays.stream(investments.getResults().get(0).getTransactions()).findFirst().get().getId();

    // get investment by existing id
    Response<Investment> investmentResponse = client.service().getInvestment(firstInvestmentId)
      .execute();
    
    // expect investment response to be valid & match the requested id
    assertTrue(investmentResponse.isSuccessful());
    Investment investment = investmentResponse.body();
    assertNotNull(investment);
    assertEquals(investment.getId(), firstInvestmentId);
    assertEquals(
      Arrays.stream(investment.getTransactions()).findFirst().get().getId(), 
      firstInvestmentTransactionId
    );

  }
}
