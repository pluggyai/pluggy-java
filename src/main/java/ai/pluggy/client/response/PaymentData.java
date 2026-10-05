package ai.pluggy.client.response;

import lombok.Data;

@Data
public class PaymentData {
  TransactionPaymentParticipant payer;
  TransactionPaymentParticipant receiver;
  String paymentMethod;
  String referenceNumber;
  String reason;
  BoletoMetadata boletoMetadata;
  /**
   * Authentication code of the payment receipt, as printed by the institution on the proof of
   * payment. Can be present for any payment method.
   */
  String authenticationCode;
  /** String submitted by the receiver when generating the payment request. */
  String receiverReferenceId;
}
