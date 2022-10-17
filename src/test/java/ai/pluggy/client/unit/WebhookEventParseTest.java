package ai.pluggy.client.unit;

import ai.pluggy.client.response.WebhookEventPayload;
import ai.pluggy.client.response.WebhookEventTriggeredBy;
import ai.pluggy.client.response.WebhookEventType;
import org.junit.jupiter.api.Test;
import ai.pluggy.utils.Utils;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class WebhookEventParseTest {

  @Test
  void webhookEventParse_jsonPayload_ok() throws IOException {
    String webhookNotificationJson = "{\n" +
      "  \"event\":\"item/created\",\n" +
      "  \"id\":\"a5c763cb-0952-457b-9936-630f79c5b016\",\n" +
      "  \"itemId\":\"a5c763cb-0952-457b-9936-630f79c5b016\",\n" +
      "  \"triggeredBy\": \"USER\"\n" +
      "}";

    WebhookEventPayload webhookPayload = Utils.parseWebhookEventPayload(webhookNotificationJson);
    
    assertEquals(WebhookEventType.ITEM_CREATED, webhookPayload.getEvent());
    assertEquals("a5c763cb-0952-457b-9936-630f79c5b016", webhookPayload.getId());
    assertEquals("a5c763cb-0952-457b-9936-630f79c5b016", webhookPayload.getItemId());
    assertEquals(WebhookEventTriggeredBy.USER, webhookPayload.getTriggeredBy());
  }
}
