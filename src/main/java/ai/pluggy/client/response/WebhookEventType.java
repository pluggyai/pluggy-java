package ai.pluggy.client.response;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum WebhookEventType {
  @SerializedName("item/created")
  ITEM_CREATED("item/created"),
  @SerializedName("item/updated")
  ITEM_UPDATED("item/updated"),
  @SerializedName("item/error")
  ITEM_ERROR("item/error"),
  @SerializedName("item/deleted")
  ITEM_DELETED("item/deleted"),
  @SerializedName("item/waiting_user_input")
  ITEM_WAITING_USER_INPUT("item/waiting_user_input"),
  @SerializedName("item/login_succeeded")
  ITEM_LOGIN_SUCCEEDED("item/login_succeeded"),
  @SerializedName("connector/status_updated")
  CONNECTOR_STATUS_UPDATED("connector/status_updated"),
  @SerializedName("all")
  ALL("all");

  @Getter
  private String value;
}
