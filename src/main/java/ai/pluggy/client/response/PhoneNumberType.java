package ai.pluggy.client.response;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum PhoneNumberType {
  @SerializedName("Personal")
  PERSONAL("Personal"),
  @SerializedName("Work")
  WORK("Work"),
  @SerializedName("Residencial")
  RESIDENCIAL("Residencial");

  @Getter
  private String value;
}
