package ai.pluggy.client.response;

import lombok.Data;

@Data
public class WebhookEventPayload {
  /**
  * @deprecated use eventId instead to identify the event.
  */
  @Deprecated
  String id; 
  String eventId;
  WebhookEventType event;
  String itemId;
  ItemError error;
  WebhookData data;
  WebhookEventTriggeredBy triggeredBy;
  /** Pix por biometria enrollment events only: the enrollment (linked account). */
  String enrollmentId;
  /**
   * Pix por biometria enrollment events only: the Payment Customer of the payment request where
   * the link was created; null when the payer typed their CPF.
   */
  String customerId;
  /** Pix por biometria enrollment events only: the payment request where the link was created. */
  String paymentRequestId;
}
