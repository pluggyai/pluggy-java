package ai.pluggy.client.response;

import lombok.Data;

@Data
public class PhoneNumber {

  // "Personal", "Work", "Residencial"
  String type;
  String value;
}
