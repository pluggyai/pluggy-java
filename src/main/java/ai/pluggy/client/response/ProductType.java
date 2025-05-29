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

  @SerializedName("MOVE_SECURITY")
  MOVE_SECURITY("MOVE_SECURITY");

  @Getter
  private String value;
}