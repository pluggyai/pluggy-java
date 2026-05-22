package ai.pluggy.client.response;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum OtherDocumentType {
  @SerializedName("CNH")
  CNH("CNH"),
  @SerializedName("RG")
  RG("RG"),
  @SerializedName("NIF")
  NIF("NIF"),
  @SerializedName("RNE")
  RNE("RNE"),
  @SerializedName("OTHER")
  OTHER("OTHER");

  @Getter
  private String value;
}
