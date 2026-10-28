package ai.pluggy.client.response;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Who revoked a Pix por biometria enrollment.
 *
 * <p>A value added server-side after this SDK release deserializes as {@code null}.
 */
@AllArgsConstructor
public enum PixBiometricRevokedBy {

  /** The payer or the institution, at the bank. */
  @SerializedName("BANK")
  BANK("BANK"),

  /** You, through the API. */
  @SerializedName("CLIENT")
  CLIENT("CLIENT"),

  /** Older links only. */
  @SerializedName("PAYER")
  PAYER("PAYER");

  @Getter
  private String value;
}
