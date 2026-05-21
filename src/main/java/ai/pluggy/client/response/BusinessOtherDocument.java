package ai.pluggy.client.response;

import java.util.Date;

import lombok.Builder;
import lombok.Data;

/**
 * Additional document for businesses headquartered abroad and not required to register a CNPJ.
 */
@Data
@Builder
public class BusinessOtherDocument {

  /** Type of the document (e.g. 'EIN'). */
  String type;
  String number;
  /** Issuing country in alpha3 ISO-3166 format. */
  String country;
  Date expirationDate;
}
