package ai.pluggy.client.response;

import lombok.Data;

@Data
public class ErrorResponse {

  String message;
  Integer code;
}
