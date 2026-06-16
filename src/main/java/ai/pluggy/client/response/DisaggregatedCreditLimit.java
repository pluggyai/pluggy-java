package ai.pluggy.client.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DisaggregatedCreditLimit {

  CreditCardLimitLineName lineName;
  String limitAmountReason;
  Double customizedLimitAmount;
  String customizedLimitAmountCurrencyCode;
}
