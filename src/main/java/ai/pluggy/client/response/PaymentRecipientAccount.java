package ai.pluggy.client.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Bank account of a payment recipient. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRecipientAccount {

  /** Branch (agency). */
  String branch;

  String number;

  /**
   * Account type, documented as {@code CHECKING_ACCOUNT}, {@code SAVINGS_ACCOUNT} or
   * {@code GUARANTEED_ACCOUNT}.
   */
  String type;
}
