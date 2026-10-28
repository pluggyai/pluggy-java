package ai.pluggy.client.response;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Payment intent. Only the fields the SDK models are mapped; the rest of the payload is ignored. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentIntent {

  String id;

  /**
   * Payment intent status.
   *
   * <p>A String rather than an enum: new values may appear, and an enum would turn them into null
   * instead of passing them through.
   */
  String status;

  PaymentRequest paymentRequest;

  Connector connector;

  /** Url to authorize the payment intent. */
  String consentUrl;

  /** How the payment is made; absent on older payloads. */
  PaymentMethod paymentMethod;

  /** Pix por biometria linked account the payment was made with; null for other payment methods. */
  String enrollmentId;

  Date createdAt;

  Date updatedAt;
}
