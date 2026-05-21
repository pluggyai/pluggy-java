package ai.pluggy.client.response;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum Sex {
  @SerializedName("FEMALE")
  FEMALE("FEMALE"),
  @SerializedName("MALE")
  MALE("MALE"),
  @SerializedName("OTHER")
  OTHER("OTHER");

  @Getter
  private String value;
}
