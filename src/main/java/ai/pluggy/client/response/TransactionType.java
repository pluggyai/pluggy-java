package ai.pluggy.client.response;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum TransactionType {
  @SerializedName("DEBIT")
  DEBIT("DEBIT"),
  @SerializedName("CREDIT")
  CREDIT("CREDIT");

  @Getter
  private String value;
}
