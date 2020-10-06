package ai.pluggy.client.response;

import lombok.Data;

@Data
public class Email {

  // "Personal", "Work"
  String type;
  String value;
}
