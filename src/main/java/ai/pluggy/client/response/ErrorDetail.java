package ai.pluggy.client.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorDetail {

  String code;
  String parameter;
  String message;
}
