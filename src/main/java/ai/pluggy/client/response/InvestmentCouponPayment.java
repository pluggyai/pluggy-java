package ai.pluggy.client.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Coupon-payment schedule for coupon-bearing fixed income / Treasury bonds.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvestmentCouponPayment {

  /** Whether the paper pays periodic coupons. */
  Boolean hasCoupon;

  /** Frequency of coupon payments: {@code MONTHLY}, {@code QUARTERLY}, {@code SEMESTERLY}, {@code YEARLY} or {@code IRREGULAR}. */
  String periodicity;

  /** Free-text detail when periodicity is {@code IRREGULAR}. */
  String additionalInfo;
}
