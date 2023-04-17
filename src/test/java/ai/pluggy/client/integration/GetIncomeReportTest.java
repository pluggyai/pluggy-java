package ai.pluggy.client.integration;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Objects;

import org.junit.jupiter.api.Test;

import static ai.pluggy.client.integration.helper.ItemHelper.createPluggyBankItem;
import static ai.pluggy.client.integration.helper.ItemHelper.getItemStatus;
import ai.pluggy.client.integration.util.Poller;
import ai.pluggy.client.response.IncomeReportResponse;
import ai.pluggy.client.response.ItemResponse;
import lombok.SneakyThrows;
import retrofit2.Response;

public class GetIncomeReportTest extends BaseApiIntegrationTest {
  @SneakyThrows
  @Test
  void getIncomeReport_byExistingItemId_ok() {
    ItemResponse pluggyBankExecution = createPluggyBankItem(client);
    itemsIdCreated.add(pluggyBankExecution.getId());

    // poll check of connector item status until it's completed (status: "UPDATED")
    Poller.pollRequestUntil(
        () -> getItemStatus(client, pluggyBankExecution.getId()),
        (ItemResponse itemResponse) -> Objects.equals(itemResponse.getStatus(), "UPDATED"),
        500, 30000);

    // get income report from exising item
    Response<IncomeReportResponse> incomeReportResponse = client.service()
        .getIncomeReport(pluggyBankExecution.getId())
        .execute();

    System.out.println(pluggyBankExecution);
    System.out.println(incomeReportResponse.body());

    // expect income report response to be valid
    assertTrue(incomeReportResponse.isSuccessful());
    IncomeReportResponse incomeReport = incomeReportResponse.body();
    assertNotNull(incomeReport);

    // expect income report to have a valid url
    assertNotNull(incomeReport.getResults().get(0).getUrl());

    // expect income report to have a valid year
    assertNotNull(incomeReport.getResults().get(0).getYear());
  }
}