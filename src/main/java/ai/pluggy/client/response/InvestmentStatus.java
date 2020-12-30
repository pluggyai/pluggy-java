package ai.pluggy.client.response;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum InvestmentStatus {
  @SerializedName("ACTIVE")
  ACTIVE("ACTIVE"),
  @SerializedName("PENDING")
  PENDING("PENDING"),
  @SerializedName("TOTAL_WITHDRAWAL")
  TOTAL_WITHDRAWAL("TOTAL_WITHDRAWAL");

  @Getter
  private String value;
}
