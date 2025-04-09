package ai.pluggy.client.response;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum ConnectorStatus {

  @SerializedName("ONLINE")
  ONLINE("ONLINE"),

  @SerializedName("OFFLINE")
  OFFLINE("OFFLINE"),

  @SerializedName("UNSTABLE")
  UNSTABLE("UNSTABLE");

  @Getter
  private String value;
}