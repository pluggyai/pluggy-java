package ai.pluggy.client.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Additional credit card associated with the main one.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdditionalCard {

  /** Number of the additional credit card. */
  String number;
}
