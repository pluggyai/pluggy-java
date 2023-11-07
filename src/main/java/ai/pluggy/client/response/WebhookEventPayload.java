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
}
