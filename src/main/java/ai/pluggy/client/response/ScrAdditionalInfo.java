package ai.pluggy.client.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Complementary information reported for an SCR operation group.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScrAdditionalInfo {

  /** Type of the complementary information. */
  String tp;

  /** Code of the complementary information. */
  String cd;

  /** Number of operations grouped under this entry. */
  Integer qtd;
}
