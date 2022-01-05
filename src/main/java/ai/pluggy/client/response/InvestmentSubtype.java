package ai.pluggy.client.response;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum InvestmentSubtype {
  /* COE_INVESTMENT_SUBTYPES */
  @SerializedName("STRUCTURED_NOTE")
  STRUCTURED_NOTE("STRUCTURED_NOTE"),
  
  /* MUTUAL_FUND_INVESTMENT_SUBTYPES */
  @SerializedName("INVESTMENT_FUND")
  INVESTMENT_FUND("INVESTMENT_FUND"),
  
  /* SECURITY_INVESTMENT_SUBTYPES */
  @SerializedName("RETIREMENT")
  RETIREMENT("RETIREMENT"),
  
  /* EQUITY_INVESTMENT_SUBTYPES */
  @SerializedName("STOCK")
  STOCK("STOCK"),
  @SerializedName("ETF")
  ETF("ETF"),
  @SerializedName("REAL_STATE_FUND")
  REAL_STATE_FUND("REAL_STATE_FUND"),
  @SerializedName("BDR")
  BDR("BDR"),
  @SerializedName("DERIVATIVES")
  DERIVATIVES("DERIVATIVES"),

  /* FIXED_INCOME_INVESTMENT_SUBTYPES */
  @SerializedName("TREASURY")
  TREASURY("TREASURY"),
  @SerializedName("LCI")
  LCI("LCI"),
  @SerializedName("LCA")
  LCA("LCA"),
  @SerializedName("CDB")
  CDB("CDB"),
  @SerializedName("CRI")
  CRI("CRI"),
  @SerializedName("CRA")
  CRA("CRA"),
  @SerializedName("CORPORATE_DEBT")
  CORPORATE_DEBT("CORPORATE_DEBT"),
  @SerializedName("LC")
  LC("LC"),
  @SerializedName("DEBENTURES")
  DEBENTURES("DEBENTURES"),

  /* UNCATEGORIZED */
  @SerializedName("OTHER")
  OTHER("OTHER");
  
  @Getter
  private String value;
}
