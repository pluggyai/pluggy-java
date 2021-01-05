package ai.pluggy.client.response;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum ConnectorType {
  @SerializedName("INVESTMENT")
  INVESTMENT("INVESTMENT"),
  @SerializedName("BUSINESS_BANK")
  BUSINESS_BANK("BUSINESS_BANK"),
  @SerializedName("PERSONAL_BANK")
  PERSONAL_BANK("PERSONAL_BANK");

  @Getter
  private String value;
}
