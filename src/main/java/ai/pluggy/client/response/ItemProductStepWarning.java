package ai.pluggy.client.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ItemProductStepWarning {
  /** The specific warning code */
  String code;
  /** Human readable message that explains the warning */
  String message;
  /** Related error message exactly as found in the institution (if any). */
  String providerMessage;
}
