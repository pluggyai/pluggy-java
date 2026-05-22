package ai.pluggy.client.response;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum BusinessPartyPersonType {
  @SerializedName("NATURAL_PERSON")
  NATURAL_PERSON("NATURAL_PERSON"),
  @SerializedName("LEGAL_ENTITY")
  LEGAL_ENTITY("LEGAL_ENTITY");

  @Getter
  private String value;
}
