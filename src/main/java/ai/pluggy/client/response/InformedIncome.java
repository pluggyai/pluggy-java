package ai.pluggy.client.response;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Income informed by the client.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InformedIncome {

  /** Frequency: {@code DIARIA}, {@code SEMANAL}, {@code QUINZENAL}, {@code MENSAL}, {@code BIMESTRAL}, {@code TRIMESTRAL}, {@code SEMESTRAL}, {@code ANUAL} or {@code OUTROS}. */
  String frequency;

  /** Amount of the informed income. */
  Double amount;

  /** Date when the income was informed. */
  Date date;
}
