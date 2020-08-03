package ai.pluggy.client.response;

import lombok.Value;

@Value
public class ErrorResponse {
  String message;
  Integer code;
}
