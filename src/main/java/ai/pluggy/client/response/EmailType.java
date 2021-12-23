package ai.pluggy.client.response;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum EmailType {
  @SerializedName("Personal")
  PERSONAL("Personal"),
  @SerializedName("Work")
  WORK("Work");

  @Getter
  private String value;
}
