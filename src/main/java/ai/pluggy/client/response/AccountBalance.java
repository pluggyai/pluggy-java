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

  Double balance;
  String currencyCode;
  /** ISO-8601 timestamp of when the institution last updated the balance. */
  String updateDateTime;
}
