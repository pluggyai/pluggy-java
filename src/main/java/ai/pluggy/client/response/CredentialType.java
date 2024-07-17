package ai.pluggy.client.response;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum CredentialType {
  @SerializedName("text")
  TEXT("text"),
  @SerializedName("password")
  PASSWORD("password"),
  @SerializedName("number")
  NUMBER("number"),
  @SerializedName("select")
  SELECT("select"),
  @SerializedName("ethaddress")
  ETHADDRESS("ethaddress"),
  @SerializedName("hcaptcha")
  HCAPTCHA("hcaptcha");

  @Getter
  private String value;
}
