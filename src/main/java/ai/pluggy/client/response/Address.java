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
  /** District / neighborhood (bairro). */
  String district;
  /** IBGE municipality code (7 digits). The first two digits identify the Federation Unit. */
  String ibgeTownCode;
  /** Country code in alpha3 ISO-3166 format (e.g. 'BRA'). */
  String countryCode;
  /** Geographic coordinates of the address. */
  GeographicCoordinates geographicCoordinates;
}
