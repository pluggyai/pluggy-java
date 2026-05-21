package ai.pluggy.client.response;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum BusinessPartyDocumentType {
  @SerializedName("CPF")
  CPF("CPF"),
  @SerializedName("CNPJ")
  CNPJ("CNPJ"),
  @SerializedName("PASSPORT")
  PASSPORT("PASSPORT"),
  @SerializedName("OTHER_TRAVEL_DOCUMENT")
  OTHER_TRAVEL_DOCUMENT("OTHER_TRAVEL_DOCUMENT");

  @Getter
  private String value;
}
