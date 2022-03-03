package ai.pluggy.client.response;

import lombok.Data;

@Data
public class PaymentData {

  TransactionPaymentParticipant payer;
  TransactionPaymentParticipant receiver;
  String paymentMethod;
  String referenceNumber;
  String reason;
}
