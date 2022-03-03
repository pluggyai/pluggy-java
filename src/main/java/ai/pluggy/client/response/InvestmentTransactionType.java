package ai.pluggy.client.response;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum InvestmentTransactionType {
  @SerializedName("BUY")
  BUY("BUY"),
  @SerializedName("SELL")
  SELL("SELL"),
  @SerializedName("TAX")
  TAX("TAX"),
  @SerializedName("TRANSFER")
  TRANSFER("TRANSFER");
  
  @Getter
  private String value;
}
