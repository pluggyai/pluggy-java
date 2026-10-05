package ai.pluggy.client.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * CNAE code describing one of the business's economic activities.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EconomicActivity {

  /** CNAE code (7 digits, including leading zeros). */
  String code;

  /** Whether this is the main economic activity. */
  Boolean isMain;
}
