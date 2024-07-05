package ai.pluggy.client.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Email {

  EmailType type;
  String value;
}
