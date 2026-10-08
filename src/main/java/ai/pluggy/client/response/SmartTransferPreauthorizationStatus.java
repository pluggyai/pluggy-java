package ai.pluggy.client.response;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Lifecycle of a Smart Transfer preauthorization. A value added server-side after this SDK release
 * deserializes as {@code null}.
 */
@AllArgsConstructor
public enum SmartTransferPreauthorizationStatus {

  /** Created and awaiting the payer's consent (see {@code consentUrl}). */
  @SerializedName("CREATED")
  CREATED("CREATED"),

  /** The payer authorized the consent; payments can now be created under it. */
  @SerializedName("COMPLETED")
  COMPLETED("COMPLETED"),

  /** The consent was revoked after being authorized. */
  @SerializedName("REVOKED")
  REVOKED("REVOKED"),

  /** The payer rejected the consent. */
  @SerializedName("REJECTED")
  REJECTED("REJECTED"),

  /** The preauthorization flow failed unexpectedly (see {@code errorDetail}). */
  @SerializedName("ERROR")
  ERROR("ERROR");

  @Getter
  private String value;
}
