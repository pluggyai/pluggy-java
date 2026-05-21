package ai.pluggy.client.response;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum MaritalStatusCode {
  @SerializedName("SINGLE")
  SINGLE("SINGLE"),
  @SerializedName("MARRIED")
  MARRIED("MARRIED"),
  @SerializedName("WIDOWED")
  WIDOWED("WIDOWED"),
  @SerializedName("JUDICIALLY_SEPARATED")
  JUDICIALLY_SEPARATED("JUDICIALLY_SEPARATED"),
  @SerializedName("DIVORCED")
  DIVORCED("DIVORCED"),
  @SerializedName("STABLE_UNION")
  STABLE_UNION("STABLE_UNION"),
  @SerializedName("OTHER")
  OTHER("OTHER");

  @Getter
  private String value;
}
