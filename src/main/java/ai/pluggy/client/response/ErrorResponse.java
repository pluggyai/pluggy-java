package ai.pluggy.client.response;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ErrorResponse {

  String message;
  Integer code;
  List<ErrorDetail> details;
}
