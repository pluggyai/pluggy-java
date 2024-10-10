package ai.pluggy.client.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BankData {

  String transferNumber;
  Double closingBalance;
  Double automaticallyInvestedBalance;
}
