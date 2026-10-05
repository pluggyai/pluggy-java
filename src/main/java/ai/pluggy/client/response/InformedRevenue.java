package ai.pluggy.client.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Revenue (faturamento) informed by the business.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InformedRevenue {

  /** Amount of the informed revenue. */
  Double amount;

  /** Frequency: {@code DAILY}, {@code WEEKLY}, {@code BIWEEKLY}, {@code MONTHLY}, {@code BIMONTHLY}, {@code QUARTERLY}, {@code SEMIANNUAL}, {@code ANNUAL} or {@code OTHER}. */
  String frequency;

  /** Free-text complement to the frequency, when it is {@code OTHER}. */
  String frequencyAdditionalInfo;

  /** Reference year of the revenue. */
  Integer year;
}
