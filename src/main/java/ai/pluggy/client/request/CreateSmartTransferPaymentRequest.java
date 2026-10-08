package ai.pluggy.client.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

/** POST /smart-transfers/payments request body. */
@Value
@AllArgsConstructor
@Builder
public class CreateSmartTransferPaymentRequest {

  /** Preauthorization the payment is made under. Required. */
  String preauthorizationId;

  /** One of the preauthorization's recipients. Required. */
  String recipientId;

  /** Payment amount. Required. */
  Double amount;

  String description;

  String clientPaymentId;

  public CreateSmartTransferPaymentRequest(String preauthorizationId, String recipientId,
      Double amount) {
    this.preauthorizationId = preauthorizationId;
    this.recipientId = recipientId;
    this.amount = amount;
    this.description = null;
    this.clientPaymentId = null;
  }
}
