package ai.pluggy.client.response;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum CreditCardAccountFeeType {
  @SerializedName("ANNUAL_FEE")
  ANNUAL_FEE("ANNUAL_FEE"),
  @SerializedName("ATM_WITHDRAWAL_DOMESTIC")
  ATM_WITHDRAWAL_DOMESTIC("ATM_WITHDRAWAL_DOMESTIC"),
  @SerializedName("ATM_WITHDRAWAL_INTERNATIONAL")
  ATM_WITHDRAWAL_INTERNATIONAL("ATM_WITHDRAWAL_INTERNATIONAL"),
  @SerializedName("EMERGENCY_CREDIT_EVALUATION")
  EMERGENCY_CREDIT_EVALUATION("EMERGENCY_CREDIT_EVALUATION"),
  @SerializedName("CARD_REISSUE")
  CARD_REISSUE("CARD_REISSUE"),
  @SerializedName("BILL_PAYMENT_FEE")
  BILL_PAYMENT_FEE("BILL_PAYMENT_FEE"),
  @SerializedName("SMS")
  SMS("SMS"),
  @SerializedName("OTHER")
  OTHER("OTHER");

  @Getter
  private String value;
}
