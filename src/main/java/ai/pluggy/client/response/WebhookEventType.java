package ai.pluggy.client.response;

import com.google.gson.annotations.SerializedName;

public enum WebhookEventType {
  @SerializedName("item/created")
  ITEM_CREATED,
  @SerializedName("item/updated")
  ITEM_UPDATED,
  @SerializedName("item/error")
  ITEM_ERROR,
  @SerializedName("item/deleted")
  ITEM_DELETED,
  @SerializedName("item/waiting_user_input")
  ITEM_WAITING_USER_INPUT,
  @SerializedName("item/login_succeeded")
  ITEM_LOGIN_SUCCEEDED,
  @SerializedName("connector/status_updated")
  CONNECTOR_STATUS_UPDATED,
  @SerializedName("all")
  ALL;

}
