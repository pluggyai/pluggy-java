package ai.pluggy.client.response;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Status of the balance permission requested with a Smart Transfer preauthorization.
 *
 * <p>{@code AWAITING_AUTHORISATION} and {@code AUTHORISED} keep Open Finance's British spelling. A
 * value added server-side after this SDK release deserializes as {@code null}.
 */
@AllArgsConstructor
public enum SmartTransferDataConsentStatus {

  /** The user has not approved the permission yet. */
  @SerializedName("AWAITING_AUTHORISATION")
  AWAITING_AUTHORISATION("AWAITING_AUTHORISATION"),

  /** The source account balance can be read. */
  @SerializedName("AUTHORISED")
  AUTHORISED("AUTHORISED"),

  /**
   * The permission was rejected, revoked, cancelled or expired (see {@code rejectionReason}). It
   * does not come back.
   */
  @SerializedName("REJECTED")
  REJECTED("REJECTED");

  @Getter
  private String value;
}
