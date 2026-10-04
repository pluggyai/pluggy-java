package ai.pluggy.client.response;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Whether a credit card transaction was paid in a single charge or in installments.
 */
@AllArgsConstructor
public enum CreditCardAccountPaymentType {
  @SerializedName("SINGLE")
  SINGLE("SINGLE"),
  @SerializedName("INSTALLMENT")
  INSTALLMENT("INSTALLMENT");

  @Getter
  private String value;
}
