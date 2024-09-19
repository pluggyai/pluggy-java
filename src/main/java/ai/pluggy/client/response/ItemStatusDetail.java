package ai.pluggy.client.response;

import lombok.Builder;
import lombok.Data;

/**
 * Only available when item.status is 'PARTIAL_SUCCESS'.
 * Provides fine-grained information, per product, about their latest collection state.
 *
 * If a product was not requested at all, its entry will be null.
 * If it was requested, it's entry will reflect if it has been collected or not.
 *  If collected, isUpdated will be true, and lastUpdatedAt will be the Date when it happened
 *  If not collected, isUpdated will be false, and lastUpdatedAt will be null it wasn't ever collected before, or the previous date if it was.
 */
@Data
@Builder
public class ItemStatusDetail {
  /** Collection details for 'ACCOUNTS' product, or null if it was not requested at all. */
  private ItemProductState accounts;
  /** Collection details for 'CREDIT_CARDS' product, or null if it was not requested at all. */
  private ItemProductState creditCards;
  /** Collection details for account 'TRANSACTIONS' product, or null if it was not requested at all. */
  private ItemProductState transactions;
  /** Collection details for 'INVESTMENTS' product, or null if it was not requested at all. */
  private ItemProductState investments;
  /** Collection details for 'INVESTMENT_TRANSACTIONS' product, or null if it was not requested at all. */
  private ItemProductState investmentTransactions;
  /** Collection details for 'IDENTITY' product, or null if it was not requested at all. */
  private ItemProductState identity;
  /** Collection details for 'PAYMENT_DATA' product, or null if it was not requested at all. */
  private ItemProductState paymentData;
  /** Collection details for 'INCOME_REPORT' product, or null if it was not requested at all. */
  private ItemProductState incomeReports;
  /** Collection details for 'PORTFOLIO' product, or null if it was not requested at all. */
  private ItemProductState portfolio;
  /** Collection details for 'LOAN' product, or null if it was not requested at all. */
  private ItemProductState loans;
  /** Collection details for 'OPPORTUNITIES' product, or null if it was not requested at all. */
  private ItemProductState opportunities;
  /** Collection details for 'BENEFIT' product, or null if it was not requested at all. */
  private ItemProductState benefits;
}
