package ai.pluggy.client.response;

import lombok.Data;

@Data
public class Email {

  EmailType type;
  String value;
}
