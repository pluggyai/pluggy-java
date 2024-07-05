package ai.pluggy.client.response;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum ItemStatus {

  @SerializedName("UPDATING")
  UPDATING("UPDATING"),
  @SerializedName("WAITING_USER_INPUT")
  WAITING_USER_INPUT("WAITING_USER_INPUT"),
  @SerializedName("WAITING_USER_ACTION")
  WAITING_USER_ACTION("WAITING_USER_ACTION"),
  @SerializedName("MERGING")
  MERGING("MERGING"),
  @SerializedName("UPDATED")
  UPDATED("UPDATED"),
  @SerializedName("LOGIN_ERROR")
  LOGIN_ERROR("LOGIN_ERROR"),
  @SerializedName("OUTDATED")
  OUTDATED("OUTDATED");

  @Getter
  private String value;

}