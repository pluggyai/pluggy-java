package ai.pluggy.client.response;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ErrorResponse {

  String message;
  Integer code;
  /**
   * Machine-readable error identifier, when the API provides one (e.g.
   * {@code LIST_ITEMS_FEATURE_NOT_ENABLED}). Null for errors that don't carry it.
   */
  String codeDescription;
  List<ErrorDetail> details;
}
