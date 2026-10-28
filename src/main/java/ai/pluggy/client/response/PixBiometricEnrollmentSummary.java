package ai.pluggy.client.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** A Pix por biometria linked account, as embedded in a payment request. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PixBiometricEnrollmentSummary {

  /** Enrollment primary identifier. */
  String id;

  PixBiometricEnrollmentStatus status;

  /** Institution where the account is held; null when unknown. */
  PixBiometricEnrollmentConnector connector;

  /** The linked account; null until the institution reports it. */
  PixBiometricDebtor debtor;
}
