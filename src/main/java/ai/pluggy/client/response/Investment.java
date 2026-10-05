package ai.pluggy.client.response;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
  /**
   * Profit/Loss to date over the investment. The API sends a number; kept as String (Gson reads
   * the number into it) so existing callers don't break. Parse with {@code Double.valueOf}.
   */
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
  /** The date when the grace period ends (fixed-income investments only). */
  Date gracePeriodDate;
  /** B3 lot/price conversion factor (variable income). */
  Double priceFactor;
  /** Whether the product is tax-exempt (LCI, LCA, CRI, CRA, incentivized debentures). */
  Boolean taxExempt;
  /** Coupon-payment schedule for coupon-bearing fixed income / Treasury bonds. */
  InvestmentCouponPayment couponPayment;
  /** Underlying debtor of receivables-backed paper (CRI / CRA). */
  InvestmentDebtor debtor;
}
