package ai.pluggy.client.response;

import java.util.Date;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OtherDocument {

  OtherDocumentType type;
  /** Free-text complement. Populated when type is OTHER. */
  String typeAdditionalInfo;
  String number;
  String checkDigit;
  /** Free-text complement, used to record the issuing authority (e.g. 'SSP/SP') when relevant. */
  String additionalInfo;
  Date expirationDate;
}
