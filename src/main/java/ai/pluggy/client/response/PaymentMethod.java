package ai.pluggy.client.response;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * How a payment intent is made.
 *
 * <p>A value added server-side after this SDK release deserializes as {@code null}.
 */
@AllArgsConstructor
public enum PaymentMethod {

  /** Payment Initiation. */
  @SerializedName("PIS")
  PIS("PIS"),

  @SerializedName("PIX")
  PIX("PIX"),

  /** Pix por biometria. */
  @SerializedName("PIX_BIOMETRIC")
  PIX_BIOMETRIC("PIX_BIOMETRIC");

  @Getter
  private String value;
}
