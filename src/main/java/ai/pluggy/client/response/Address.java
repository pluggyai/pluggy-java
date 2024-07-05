package ai.pluggy.client.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Address {

  String fullAddress;
  String country;
  String state;
  String city;
  String postalCode;
  String primaryAddress;
  AddressType type;
}
