package ai.pluggy.client.response;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum ProductType {
  @SerializedName("ACCOUNTS")
  ACCOUNTS("ACCOUNTS"),

  @SerializedName("CREDIT_CARDS")
  CREDIT_CARDS("CREDIT_CARDS"),

  @SerializedName("TRANSACTIONS")
  TRANSACTIONS("TRANSACTIONS"),

  @SerializedName("PAYMENT_DATA")
  PAYMENT_DATA("PAYMENT_DATA"),

  @SerializedName("INVESTMENTS")
  INVESTMENTS("INVESTMENTS"),

  @SerializedName("INVESTMENTS_TRANSACTIONS")
  INVESTMENTS_TRANSACTIONS("INVESTMENTS_TRANSACTIONS"),

  @SerializedName("BROKERAGE_NOTE")
  BROKERAGE_NOTE("BROKERAGE_NOTE"),

  @SerializedName("LOANS")
  LOANS("LOANS"),

  @SerializedName("EXCHANGE_OPERATIONS")
  EXCHANGE_OPERATIONS("EXCHANGE_OPERATIONS"),

  @SerializedName("IDENTITY")
  IDENTITY("IDENTITY"),

  @SerializedName("OPPORTUNITIES")
  OPPORTUNITIES("OPPORTUNITIES"),

  @SerializedName("PORTFOLIO")
  PORTFOLIO("PORTFOLIO"),

  @SerializedName("INCOME_REPORTS")
  INCOME_REPORTS("INCOME_REPORTS"),

  @SerializedName("MOVE_SECURITY")
  MOVE_SECURITY("MOVE_SECURITY"),

  @SerializedName("ACQUIRER_OPERATIONS")
  ACQUIRER_OPERATIONS("ACQUIRER_OPERATIONS"),

  @SerializedName("BENEFITS")
  BENEFITS("BENEFITS");

  @Getter
  private String value;
}