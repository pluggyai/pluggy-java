package ai.pluggy.client.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Limits the payer set at the bank for a Pix por biometria enrollment, in BRL. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PixBiometricEnrollmentLimits {

  /** Per-payment limit. */
  Double transaction;

  /** Daily limit. */
  Double daily;

  /** Part of the daily limit already used. */
  Double dailyUsed;
}
