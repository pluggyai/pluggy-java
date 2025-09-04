package ai.pluggy.client.response;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class FinancialCharge {
  String id;
  String type;
  Double amount;
  String currencyCode;
  String additionalInfo;
}
