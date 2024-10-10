package ai.pluggy.client.response;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum CreditCardStatus {
  @SerializedName("ACTIVE")
  ACTIVE("ACTIVE"),
  @SerializedName("BLOCKED")
  BLOCKED("BLOCKED"),
  @SerializedName("CANCELLED")
  CANCELLED("CANCELLED");

  @Getter
  private String value;
}
