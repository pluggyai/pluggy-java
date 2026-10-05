package ai.pluggy.client.response;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Payment associated to a credit card bill.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillPayment {

  /** Primary identifier. */
  String id;

  /** Payment value type: {@code INSTALLMENT_PAYMENT}, {@code FULL_PAYMENT} or {@code OTHER_PAYMENT}. */
  String valueType;

  /** Date when the payment was made. */
  Date paymentDate;

  /** Payment mode used: {@code DEBIT_ACCOUNT}, {@code BANK_SLIP}, {@code PAYROLL_DEDUCTION}, {@code PIX}, or null. */
  String paymentMode;

  /** Payment amount. */
  Double amount;

  /** Currency code of the payment (e.g. {@code BRL}). */
  String currencyCode;
}
