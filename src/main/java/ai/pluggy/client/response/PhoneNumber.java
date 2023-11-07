package ai.pluggy.client.response;

import lombok.Data;

@Data
public class PhoneNumber {

  PhoneNumberType type;
  String value;
}
