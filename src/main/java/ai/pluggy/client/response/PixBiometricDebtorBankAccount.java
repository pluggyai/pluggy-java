package ai.pluggy.client.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Bank account of a Pix por biometria linked account, as the institution reported it. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PixBiometricDebtorBankAccount {

  /** Branch number. */
  String agency;

  /** Account number, masked. */
  String account;

  /** Institution name. */
  String name;
}
