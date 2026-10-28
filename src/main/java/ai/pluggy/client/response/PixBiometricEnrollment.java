package ai.pluggy.client.response;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** A Pix por biometria linked account (enrollment): the payer's account at one institution, linked to the
 * device and browser where they created the passkey. Only the payer creates one, on the payment page. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PixBiometricEnrollment {

  /** Enrollment primary identifier. Payment intents reference it as {@code enrollmentId}. */
  String id;

  PixBiometricEnrollmentStatus status;

  /** Institution where the account is held; null when unknown. */
  PixBiometricEnrollmentConnector connector;

  /** The linked account; null until the institution reports it. */
  PixBiometricDebtor debtor;

  /** Payer's CPF, masked. */
  String cpf;

  /** Payer's CNPJ, masked. */
  String cnpj;

  /**
   * Payment Customer of the payment request where the link was created; null when the payer typed
   * their CPF.
   */
  String customerId;

  /** Limits the payer set at the bank, in BRL; null until the institution reports them. */
  PixBiometricEnrollmentLimits limits;

  /** When the link expires, as set by the institution; null until reported. */
  Date expiresAt;

  /**
   * Balance-sharing permission; null when the payment request did not offer it or the payer chose
   * not to share.
   */
  PixBiometricDataConsent dataConsent;

  /** Who revoked the link; null unless {@code status} is {@code REVOKED}. */
  PixBiometricRevokedBy revokedBy;

  /** Reason the institution gave when the link was rejected; null otherwise. */
  PixBiometricErrorDetail errorDetail;

  Date createdAt;

  Date updatedAt;
}
