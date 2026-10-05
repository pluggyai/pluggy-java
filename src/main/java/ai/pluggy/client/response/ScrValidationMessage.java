package ai.pluggy.client.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Validation message raised by Bacen for an SCR request.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScrValidationMessage {

  /** Message code. */
  String codigo;

  /** Message text. */
  String mensagem;
}
