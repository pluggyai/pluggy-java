package ai.pluggy.client.response;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum TransactionStatus {
  @SerializedName("PENDING")
  PENDING("PENDING"),
  @SerializedName("POSTED")
  POSTED("POSTED");

  @Getter
  private String value;
}
