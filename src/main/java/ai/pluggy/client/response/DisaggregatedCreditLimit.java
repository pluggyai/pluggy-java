package ai.pluggy.client.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DisaggregatedCreditLimit {

  CreditCardLimitLineName lineName;
  String limitAmountReason;
  Double customizedLimitAmount;
  String customizedLimitAmountCurrencyCode;
}
