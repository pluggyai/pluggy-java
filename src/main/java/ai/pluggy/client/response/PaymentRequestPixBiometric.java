package ai.pluggy.client.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Present on Pix por biometria payment requests; null on every other kind. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequestPixBiometric {

  /** Whether the payer was offered balance sharing. */
  Boolean linkedJourney;

  /** The linked account the request was paid with; null until the request is paid. */
  PixBiometricEnrollmentSummary enrollment;
}
