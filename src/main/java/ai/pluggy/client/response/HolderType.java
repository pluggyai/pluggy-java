package ai.pluggy.client.response;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum HolderType {
  @SerializedName("MAIN")
  MAIN("MAIN"),
  @SerializedName("ADDITIONAL")
  ADDITIONAL("ADDITIONAL");

  @Getter
  private String value;
}
