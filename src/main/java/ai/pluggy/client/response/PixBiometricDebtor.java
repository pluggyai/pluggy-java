package ai.pluggy.client.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** The linked account of a Pix por biometria enrollment, as the institution reported it. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PixBiometricDebtor {

  /** Account holder name. */
  String name;

  /** Account holder CPF/CNPJ, masked. */
  String taxNumber;

  PixBiometricDebtorBankAccount bankAccount;
}
