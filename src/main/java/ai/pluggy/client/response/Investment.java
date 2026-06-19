package ai.pluggy.client.response;

import java.util.Date;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
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
  String issuerCNPJ;
  /**
   * @deprecated the API field is {@code issueDate}; this misnamed field always
   *             resolves to {@code null} under Gson's {@code IDENTITY} naming.
   *             Use {@link #issueDate} instead.
   */
  @Deprecated
  Date issuerDate;
  Date issueDate;
  Date purchaseDate;
  InvestmentStatus status;
  /**
   * @deprecated use
   *             `pluggyClient.service().getInvestmentTransactions(investmentId,
   *             searchFilters)` instead
   *             this field is null unless the application was created before
   *             2023-03-21
   */
  @Deprecated
  InvestmentTransaction[] transactions;
  InvestmentMetadata metadata;
  String providerId;
  InvestmentInstitution institution;
}
