package ai.pluggy.client.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * GET /accounts/{id}/balance response: the account balance fetched in real time from the
 * institution. Only available for Open Finance connectors.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountBalance {

  /** Available balance, same as {@code balance} in the account resource. */
  Double balance;
  /** Amount currently blocked or held on the account. Null when the institution does not report it. */
  Double blockedBalance;
  /** Amount held in the account's automatic investment facility. Null when not reported. */
  Double automaticallyInvestedBalance;
  /**
   * Accounting balance: {@code balance + blockedBalance}. Same value as {@code bankData.closingBalance}
   * in the account resource, which the API refreshes with it. Null when the institution does not report it.
   */
  Double closingBalance;
  String currencyCode;
  /** ISO-8601 timestamp of when the institution last updated the balance. */
  String updateDateTime;
}
