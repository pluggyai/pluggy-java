package ai.pluggy.client.response;

import lombok.Data;

@Data
public class ErrorDetail {

  String code;
  String parameter;
  String message;
}
