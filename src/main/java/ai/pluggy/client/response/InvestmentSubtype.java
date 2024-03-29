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
  @SerializedName("MULTIMARKET_FUND")
  MULTIMARKET_FUND("MULTIMARKET_FUND"),
  @SerializedName("FIXED_INCOME_FUND")
  FIXED_INCOME_FUND("FIXED_INCOME_FUND"),
  @SerializedName("STOCK_FUND")
  STOCK_FUND("STOCK_FUND"),
  @SerializedName("ETF_FUND")
  ETF_FUND("ETF_FUND"),
  @SerializedName("OFFSHORE_FUND")
  OFFSHORE_FUND("OFFSHORE_FUND"),
  @SerializedName("FIP_FUND")
  FIP_FUND("FIP_FUND"),
  @SerializedName("EXCHANGE_FUND")
  EXCHANGE_FUND("EXCHANGE_FUND"),
  
  /* SECURITY_INVESTMENT_SUBTYPES */
  @SerializedName("RETIREMENT")
  RETIREMENT("RETIREMENT"),
  
  /* EQUITY_INVESTMENT_SUBTYPES */
  @SerializedName("STOCK")
  STOCK("STOCK"),
  @SerializedName("ETF")
  ETF("ETF"),
  @SerializedName("REAL_ESTATE_FUND")
  REAL_ESTATE_FUND("REAL_ESTATE_FUND"),
  @SerializedName("BDR")
  BDR("BDR"),
  @SerializedName("DERIVATIVES")
  DERIVATIVES("DERIVATIVES"),
  @SerializedName("OPTION")
  OPTION("OPTION"),

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
