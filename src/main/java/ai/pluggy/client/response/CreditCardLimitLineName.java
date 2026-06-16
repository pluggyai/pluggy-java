package ai.pluggy.client.response;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum CreditCardLimitLineName {
  @SerializedName("CREDITO_A_VISTA")
  CREDITO_A_VISTA("CREDITO_A_VISTA"),
  @SerializedName("CREDITO_PARCELADO")
  CREDITO_PARCELADO("CREDITO_PARCELADO"),
  @SerializedName("SAQUE_CREDITO_BRASIL")
  SAQUE_CREDITO_BRASIL("SAQUE_CREDITO_BRASIL"),
  @SerializedName("SAQUE_CREDITO_EXTERIOR")
  SAQUE_CREDITO_EXTERIOR("SAQUE_CREDITO_EXTERIOR"),
  @SerializedName("EMPRESTIMO_CARTAO_CONSIGNADO")
  EMPRESTIMO_CARTAO_CONSIGNADO("EMPRESTIMO_CARTAO_CONSIGNADO"),
  @SerializedName("OUTROS")
  OUTROS("OUTROS");

  @Getter
  private String value;
}
