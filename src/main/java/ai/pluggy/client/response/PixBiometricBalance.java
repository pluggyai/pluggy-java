package ai.pluggy.client.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** GET /pix-biometric/enrollments/{id}/balance response: the linked account balance, in BRL. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PixBiometricBalance {

  /** Account balance in BRL. */
  Double balance;

  /** Overdraft limit of the account; null when the institution does not share it. */
  SmartTransferOverdraft overdraft;
}
