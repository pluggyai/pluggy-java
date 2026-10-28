package ai.pluggy.client.response;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Status of the balance permission of a Pix por biometria enrollment.
 *
 * <p>{@code AWAITING_AUTHORISATION} and {@code AUTHORISED} keep Open Finance's British spelling. A
 * value added server-side after this SDK release deserializes as {@code null}.
 */
@AllArgsConstructor
public enum PixBiometricDataConsentStatus {

  /** The payer has not approved the permission yet. */
  @SerializedName("AWAITING_AUTHORISATION")
  AWAITING_AUTHORISATION("AWAITING_AUTHORISATION"),

  /** The account balance can be read. */
  @SerializedName("AUTHORISED")
  AUTHORISED("AUTHORISED"),

  /** The permission was rejected. */
  @SerializedName("REJECTED")
  REJECTED("REJECTED"),

  /** The permission was revoked or cancelled. */
  @SerializedName("REVOKED")
  REVOKED("REVOKED");

  @Getter
  private String value;
}
