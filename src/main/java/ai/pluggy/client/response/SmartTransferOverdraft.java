package ai.pluggy.client.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Overdraft limit of a Smart Transfer source account, in BRL. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SmartTransferOverdraft {

  /** Overdraft limit contracted, in BRL. */
  Double contracted;

  /** Part of the overdraft limit in use, in BRL. */
  Double used;

  /** Part of the overdraft limit still available, in BRL. */
  Double available;
}
