package ai.pluggy.client.response;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Status of a Pix por biometria enrollment. {@code REJECTED}, {@code REVOKED} and {@code EXPIRED}
 * are final.
 *
 * <p>A value added server-side after this SDK release deserializes as {@code null}.
 */
@AllArgsConstructor
public enum PixBiometricEnrollmentStatus {

  /** The payer started linking and has not finished. */
  @SerializedName("CREATED")
  CREATED("CREATED"),

  /** The link is active. */
  @SerializedName("AUTHORIZED")
  AUTHORIZED("AUTHORIZED"),

  /** The link was not completed (rejected, or not finished in time). */
  @SerializedName("REJECTED")
  REJECTED("REJECTED"),

  /** Revoked at the bank or through the API. */
  @SerializedName("REVOKED")
  REVOKED("REVOKED"),

  /** The link reached its expiration date or became inactive at the institution. */
  @SerializedName("EXPIRED")
  EXPIRED("EXPIRED");

  @Getter
  private String value;
}
