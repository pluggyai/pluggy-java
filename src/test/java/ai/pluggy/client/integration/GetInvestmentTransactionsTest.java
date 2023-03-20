package ai.pluggy.client.integration;

import static ai.pluggy.client.integration.helper.InvestmentHelper.getPluggyBankInvestments;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ai.pluggy.client.request.InvestmentTransactionsSearchRequest;
import ai.pluggy.client.response.InvestmentTransactionsResponse;
import ai.pluggy.client.response.InvestmentsResponse;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import retrofit2.Response;

public class GetInvestmentTransactionsTest extends BaseApiIntegrationTest {
    @SneakyThrows
    @Test
    void getTransactions_byExistingInvestmentId_ok() {
        // precondition: get existing investments
        InvestmentsResponse investments = getPluggyBankInvestments(client);
        // get all investments ids to iterate over them and get the transactions
        String firstInvestmentId = investments.getResults().get(0).getId();

        // get investment transactions
        Response<InvestmentTransactionsResponse> investmentTransactionsResponse = client.service()
                .getInvestmentTransactions(firstInvestmentId, new InvestmentTransactionsSearchRequest().pageSize(20))
                .execute();

        // expect investment response to be valid
        assertTrue(investmentTransactionsResponse.isSuccessful());
        InvestmentTransactionsResponse investmentTransactions = investmentTransactionsResponse.body();
        assertNotNull(investmentTransactions);
        // expect investment transactions to be valid and have at least one transaction
        assertNotNull(investmentTransactions.getResults());
        assertTrue(investmentTransactions.getResults().size() > 0);
    }
}
