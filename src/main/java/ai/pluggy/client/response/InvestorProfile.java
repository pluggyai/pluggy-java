package ai.pluggy.client.response;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum InvestorProfile {
    @SerializedName("Conservative")
    CONSERVATIVE("Conservative"),

    @SerializedName("Moderate")
    MODERATE("Moderate"),

    @SerializedName("Aggressive")
    AGGRESSIVE("Aggressive");

    @Getter
    private String value;
}
