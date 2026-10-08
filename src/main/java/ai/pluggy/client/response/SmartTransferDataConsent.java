package ai.pluggy.client.response;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Permission to read the source account balance, requested together with a Smart Transfer
 * preauthorization created with {@code linkedJourney}. Its status is refreshed from the
 * institution when the preauthorization is retrieved by id; the list returns the last known
 * status.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SmartTransferDataConsent {

  SmartTransferDataConsentStatus status;

  /**
   * Why the permission is {@code REJECTED}, for example {@code CUSTOMER_MANUALLY_REJECTED},
   * {@code CUSTOMER_MANUALLY_REVOKED}, {@code CONSENT_EXPIRED} or
   * {@code CONSENT_MAX_DATE_REACHED}; null otherwise.
   *
   * <p>A String rather than an enum: new values may appear, and an enum would turn them into null
   * instead of passing them through.
   */
  String rejectionReason;

  /** When the status last changed. */
  Date updatedAt;
}
