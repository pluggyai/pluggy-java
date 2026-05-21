package ai.pluggy.client.response;

import lombok.Data;

@Data
public class PhoneNumber {

  PhoneNumberType type;
  String value;
  /** International dialing code (DDI). Populated when different from '55'. */
  String countryCallingCode;
  /** Area code (DDD) of the phone. */
  String areaCode;
  /** Extension number, when part of the phone identification. */
  String extension;
  /** Additional info related to the source phone type. */
  String additionalInfo;
}
