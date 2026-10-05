package ai.pluggy.client.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Guarantee backing an SCR operation group.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScrGuarantee {

  /** Bacen's code for the guarantee type. */
  String tp;

  /** Number of operations grouped under this type. */
  Integer qtd;
}
