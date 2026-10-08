package ai.pluggy.client.response;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Bank-account payment recipient. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRecipient {

  /** Recipient discriminator, always {@code BANK_ACCOUNT}. */
  String type;

  String id;

  /** Account owner tax number, CPF or CNPJ (only numbers). */
  String taxNumber;

  /** Account owner name. */
  String name;

  PaymentInstitution paymentInstitution;

  /** Whether the recipient is the default one. */
  Boolean isDefault;

  PaymentRecipientAccount account;

  /** Pix key associated with the recipient, or null. */
  String pixKey;

  Date createdAt;

  Date updatedAt;
}
