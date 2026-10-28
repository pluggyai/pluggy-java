package ai.pluggy.client.response;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Permission to read the account balance of a Pix por biometria enrollment. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PixBiometricDataConsent {

  PixBiometricDataConsentStatus status;

  /**
   * Why the permission was rejected; null otherwise.
   *
   * <p>A String rather than an enum: new values may appear, and an enum would turn them into null
   * instead of passing them through.
   */
  String rejectionReason;

  /** When the status last changed. */
  Date updatedAt;
}
