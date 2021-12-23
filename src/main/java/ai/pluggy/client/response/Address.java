package ai.pluggy.client.response;

import lombok.Data;

@Data
public class Address {

  String fullAddress;
  String country;
  String state;
  String city;
  String postalCode;
  String primaryAddress;
  AddressType type;
}
