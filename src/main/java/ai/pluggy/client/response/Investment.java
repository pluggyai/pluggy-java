package ai.pluggy.client.response;

import java.util.Date;
import lombok.Data;

@Data
public class Investment {
  String id;
  String itemId;
  String code;
  String isin;
  String number;
  String name;
  String owner;
  InvestmentType type;
  InvestmentSubtype subtype;
  String currencyCode;
  Double rate;
  String rateType;
  Double fixedAnnualRate;
  Double annualRate;
  Double lastTwelveMonthsRate;
  Double lastMonthRate;
  Double balance;
  Double value;
  Double quantity;
  Double amount;
  Date date;
  Date dueDate;
  Double taxes;
  Double taxes2;
  Double amountOriginal;
  String amountProfit = null;
  Double amountWithdrawal;
  String issuer;
  Date issuerDate;
  InvestmentStatus status;
  /**
   * @deprecated use `pluggyClient.service().getInvestmentTransactions(investmentId, searchFilters)` instead
   * this field is null unless the application was created before 2023-03-21
   */
  @Deprecated
  InvestmentTransaction[] transactions;
  InvestmentMetadata metadata;
  String providerId;
  InvestmentInstitution institution;
}
