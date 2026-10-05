package ai.pluggy.client.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Account of the client with valid consent.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdentityFinancialRelationshipAccount {

  /** COMPE code of the account. */
  String compeCode;

  /** Branch code of the account. */
  String branchCode;

  /** Number of the account. */
  String number;

  /** Check digit of the account. */
  String checkDigit;

  /** Type of the account. */
  String type;

  /** Subtype of the account. */
  String subtype;
}
