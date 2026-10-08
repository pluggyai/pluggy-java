package ai.pluggy.client.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Limits of a Smart Transfer preauthorization. Used both when creating a preauthorization and in
 * the preauthorization response.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SmartTransferPreauthorizationConfiguration {

  /** Maximum amount reachable by the sum of all payments made under the consent. */
  Double totalAllowedAmount;

  /** Maximum amount for each payment made under the consent. */
  Double transactionLimit;

  SmartTransferPeriodicLimits periodicLimits;
}
