package ai.pluggy.client.response;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Payment request. Only the fields the SDK models are mapped; the rest of the payload is ignored. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {

  String id;

  /** Requested amount, in BRL. */
  Double amount;

  String description;

  /**
   * Payment request status.
   *
   * <p>A String rather than an enum: new values may appear, and an enum would turn them into null
   * instead of passing them through.
   */
  String status;

  /** Your own identifier for the payment. */
  String clientPaymentId;

  PaymentRequestCallbackUrls callbackUrls;

  PaymentRecipient recipient;

  /** Url where the payer starts the payment. */
  String paymentUrl;

  Boolean isSandbox;

  /** Pix por biometria details; null (or absent) on other kinds of payment request. */
  PaymentRequestPixBiometric pixBiometric;

  Date createdAt;

  Date updatedAt;
}
