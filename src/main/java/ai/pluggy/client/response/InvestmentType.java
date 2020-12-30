package ai.pluggy.client.response;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum InvestmentType {
  @SerializedName("MUTUAL_FUND")
  MUTUAL_FUND("MUTUAL_FUND"),
  @SerializedName("SECURITY")
  SECURITY("SECURITY"),
  @SerializedName("EQUITY")
  EQUITY("EQUITY"),
  @SerializedName("FIXED_INCOME")
  FIXED_INCOME("FIXED_INCOME"),
  @SerializedName("ETF")
  ETF("ETF"),
  @SerializedName("OTHER")
  OTHER("OTHER");

  @Getter
  private String value;
}
