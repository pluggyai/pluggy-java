package ai.pluggy.client.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReservedBalanceAmount {

  Double amount;
  String currencyCode;
  ReservedBalanceRemuneration remuneration;
}
