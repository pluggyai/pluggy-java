package ai.pluggy.client.response;

import java.util.List;
import lombok.Data;

@Data
public class ErrorResponse {

  String message;
  Integer code;
  List<ErrorDetail> details;
}
