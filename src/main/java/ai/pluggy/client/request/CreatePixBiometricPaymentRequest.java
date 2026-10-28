package ai.pluggy.client.request;

import ai.pluggy.client.response.PaymentRequestCallbackUrls;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

/**
 * POST /payments/requests/pix-biometric request body. Pix por biometria has no sandbox, so
 * {@code isSandbox} is not offered.
 */
@Value
@AllArgsConstructor
@Builder
public class CreatePixBiometricPaymentRequest {

  /** Amount in BRL, with up to two decimal places. Required. */
  Double amount;

  /** Recipient that receives the payment. Bank account recipients only. Required. */
  String recipientId;

  /** Shown to the payer. */
  String description;

  /**
   * Payment Customer that identifies the payer. Without it the payer types their CPF on the
   * payment page.
   */
  String customerId;

  /** Your own identifier for the payment. */
  String clientPaymentId;

  PaymentRequestCallbackUrls callbackUrls;

  /**
   * Offer the payer to also share their account balance and overdraft limit while the link lasts.
   */
  Boolean linkedJourney;

  public CreatePixBiometricPaymentRequest(Double amount, String recipientId) {
    this.amount = amount;
    this.recipientId = recipientId;
    this.description = null;
    this.customerId = null;
    this.clientPaymentId = null;
    this.callbackUrls = null;
    this.linkedJourney = null;
  }
}
