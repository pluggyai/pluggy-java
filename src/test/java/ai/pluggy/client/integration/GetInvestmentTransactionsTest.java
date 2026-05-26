package ai.pluggy.client.integration;

import static ai.pluggy.client.integration.helper.InvestmentHelper.getPluggyBankInvestments;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import ai.pluggy.client.request.InvestmentTransactionsSearchRequest;
import ai.pluggy.client.response.Investment;
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

        // walk investments to find one with transactions — the sandbox's first
        // investment may have none even when others do, so we don't hard-bind to index 0.
        InvestmentTransactionsResponse investmentTransactions = null;
        String investmentIdWithTxs = null;
        for (Investment investment : investments.getResults()) {
            Response<InvestmentTransactionsResponse> response = client.service()
                    .getInvestmentTransactions(investment.getId(), new InvestmentTransactionsSearchRequest().pageSize(20))
                    .execute();
            assertTrue(response.isSuccessful());
            InvestmentTransactionsResponse body = response.body();
            assertNotNull(body);
            assertNotNull(body.getResults());
            if (!body.getResults().isEmpty()) {
                investmentTransactions = body;
                investmentIdWithTxs = investment.getId();
                break;
            }
        }

        // skip if no investment in the sandbox has any transactions
        assumeTrue(investmentTransactions != null,
                String.format("skipping: no investments in the sandbox have any transactions (checked '%d' investments)",
                        investments.getResults().size()));

        assertTrue(investmentTransactions.getResults().size() > 0,
                String.format("expected investment id '%s' to have at least 1 transaction", investmentIdWithTxs));
    }
}
