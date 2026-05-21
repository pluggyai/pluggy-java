package ai.pluggy.client.response;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum BusinessPartyType {
  @SerializedName("PARTNER")
  PARTNER("PARTNER"),
  @SerializedName("ADMINISTRATOR")
  ADMINISTRATOR("ADMINISTRATOR");

  @Getter
  private String value;
}
