package ai.pluggy.client.response;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Passport {

  String number;
  /** Issuing country in alpha3 ISO-3166 format. */
  String country;
  Date issueDate;
  Date expirationDate;
}
