package ai.pluggy.client.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Underlying debtor of receivables-backed paper (CRI / CRA).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvestmentDebtor {

  /** Name of the underlying debtor. */
  String name;
}
