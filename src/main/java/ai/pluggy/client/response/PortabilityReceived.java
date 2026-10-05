package ai.pluggy.client.response;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Salary portability received by the institution from a previous paycheck bank (banco-folha). Approved requests only; an entry does not guarantee an active link.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PortabilityReceived {

  /** Employer name as received in the portability message. */
  String employerName;

  /** Employer document (CPF or CNPJ). */
  String employerDocument;

  /** CNPJ of the bank that holds the paycheck account. */
  String paycheckBankDetainerCnpj;

  /** ISPB of the bank that holds the paycheck account. */
  String paycheckBankDetainerIspb;

  /** Date the portability was approved. */
  Date portabilityApprovalDate;
}
