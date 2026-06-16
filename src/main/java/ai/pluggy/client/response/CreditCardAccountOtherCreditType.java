package ai.pluggy.client.response;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum CreditCardAccountOtherCreditType {
  @SerializedName("REVOLVING_CREDIT")
  REVOLVING_CREDIT("REVOLVING_CREDIT"),
  @SerializedName("BILL_INSTALLMENT")
  BILL_INSTALLMENT("BILL_INSTALLMENT"),
  @SerializedName("LOAN")
  LOAN("LOAN"),
  @SerializedName("OTHER")
  OTHER("OTHER");

  @Getter
  private String value;
}
