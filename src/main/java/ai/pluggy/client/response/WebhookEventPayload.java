package ai.pluggy.client.response;

import lombok.Data;

@Data
public class WebhookEventPayload {
  String id;
  WebhookEventType event;
  String url;
  String itemId;
  Object error;
  Object data;
}
