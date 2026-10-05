package ai.pluggy.client.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemProductStepWarning {
  /** The specific warning code */
  String code;
  /** Human readable message that explains the warning */
  String message;
  /** Related error message exactly as found in the institution (if any). */
  String providerMessage;
}
