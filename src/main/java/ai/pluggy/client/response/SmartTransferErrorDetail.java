package ai.pluggy.client.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Why a Smart Transfer preauthorization or payment ended in error. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SmartTransferErrorDetail {

  /**
   * Error code. For payments it is documented as {@code INFRASTRUCTURE_FAILURE},
   * {@code PAYMENT_DIFFERENT_FROM_CONSENT}, {@code UNKNOWN_ERROR}, {@code INVALID_PAYMENT_DETAIL},
   * {@code PAYMENT_REJECTED_BY_HOLDER} or {@code PAYMENT_REJECTED_BY_SPI}.
   *
   * <p>A String rather than an enum: other values can appear, and an enum would turn them into
   * null instead of passing them through.
   */
  String code;

  String description;

  String detail;
}
