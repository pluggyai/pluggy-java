package ai.pluggy.client.response;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Paycheck-bank (banco-folha) link to an employer, active or formerly active.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaycheckBankLink {

  /** Employer name as registered when the paycheck account was opened. */
  String employerName;

  /** Employer document (CPF or CNPJ). */
  String employerDocument;

  /** CNPJ of the institution providing the paycheck service. */
  String paycheckBankCnpj;

  /** ISPB of the institution providing the paycheck service. */
  String paycheckBankIspb;

  /** Date the paycheck account was opened. */
  Date accountOpeningDate;
}
