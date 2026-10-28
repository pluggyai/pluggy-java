package ai.pluggy.client.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Where to redirect the payer after a payment request flow. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequestCallbackUrls {

  /** Url to redirect to after the payment was completed. */
  String success;

  /** Url to redirect to when the payment is pending. */
  String pending;

  /** Url to redirect to after the payment ended in error. */
  String error;
}
