package ai.pluggy.client.response;

import java.util.Date;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Passport {

  String number;
  /** Issuing country in alpha3 ISO-3166 format. */
  String country;
  Date issueDate;
  Date expirationDate;
}
