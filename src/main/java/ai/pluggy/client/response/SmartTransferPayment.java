package ai.pluggy.client.response;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Transfer made under a Smart Transfer preauthorization. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SmartTransferPayment {

  String id;

  String preauthorizationId;

  SmartTransferPaymentStatus status;

  Double amount;

  String description;

  /** Recipient of the transfer; may be null until the payment is associated with one. */
  PaymentRecipient recipient;

  String clientPaymentId;

  Date createdAt;

  Date updatedAt;

  /** Set when the payment ended in error. */
  SmartTransferErrorDetail errorDetail;
}
