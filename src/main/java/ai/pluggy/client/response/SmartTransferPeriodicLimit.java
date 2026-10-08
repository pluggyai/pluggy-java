package ai.pluggy.client.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Transactional limit for one period of a Smart Transfer preauthorization. When sent, at least one
 * of the two fields must be set.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SmartTransferPeriodicLimit {

  /** Maximum number of transactions allowed in the period. */
  Integer quantityLimit;

  /** Maximum amount to be transacted in the period. */
  Double transactionLimit;
}
