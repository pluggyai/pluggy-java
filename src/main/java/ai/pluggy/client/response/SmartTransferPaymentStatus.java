package ai.pluggy.client.response;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Lifecycle of a Smart Transfer payment. A value added server-side after this SDK release
 * deserializes as {@code null}.
 */
@AllArgsConstructor
public enum SmartTransferPaymentStatus {

  /** Accepted under the preauthorization and ready to be processed. */
  @SerializedName("CONSENT_AUTHORIZED")
  CONSENT_AUTHORIZED("CONSENT_AUTHORIZED"),

  /** The preauthorization was rejected at execution time. */
  @SerializedName("CONSENT_REJECTED")
  CONSENT_REJECTED("CONSENT_REJECTED"),

  /** Submitted to the institution and awaiting confirmation. */
  @SerializedName("PAYMENT_PENDING")
  PAYMENT_PENDING("PAYMENT_PENDING"),

  /** Accepted, but still needs an additional authorization. */
  @SerializedName("PAYMENT_PARTIALLY_ACCEPTED")
  PAYMENT_PARTIALLY_ACCEPTED("PAYMENT_PARTIALLY_ACCEPTED"),

  /** The settlement is being processed. */
  @SerializedName("PAYMENT_SETTLEMENT_PROCESSING")
  PAYMENT_SETTLEMENT_PROCESSING("PAYMENT_SETTLEMENT_PROCESSING"),

  /** The funds were debited from the payer account and are awaiting clearing. */
  @SerializedName("PAYMENT_SETTLEMENT_DEBTOR_ACCOUNT")
  PAYMENT_SETTLEMENT_DEBTOR_ACCOUNT("PAYMENT_SETTLEMENT_DEBTOR_ACCOUNT"),

  /** Confirmed by the institution. */
  @SerializedName("PAYMENT_COMPLETED")
  PAYMENT_COMPLETED("PAYMENT_COMPLETED"),

  /** Rejected after the consent was authorized. */
  @SerializedName("PAYMENT_REJECTED")
  PAYMENT_REJECTED("PAYMENT_REJECTED"),

  /** An unexpected error occurred during the flow (see {@code errorDetail}). */
  @SerializedName("ERROR")
  ERROR("ERROR"),

  /** The payment was canceled. */
  @SerializedName("CANCELED")
  CANCELED("CANCELED");

  @Getter
  private String value;
}
