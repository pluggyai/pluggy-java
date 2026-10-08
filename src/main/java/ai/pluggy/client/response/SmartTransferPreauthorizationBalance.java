package ai.pluggy.client.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * GET /smart-transfers/preauthorizations/{id}/balance response: the source account balance, read
 * in real time from the institution.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SmartTransferPreauthorizationBalance {

  /** Source account balance in BRL. */
  Double balance;

  /** Overdraft limit of the source account; null when the institution does not share it. */
  SmartTransferOverdraft overdraft;
}
