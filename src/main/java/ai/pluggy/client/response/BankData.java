package ai.pluggy.client.response;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BankData {

  String transferNumber;
  Double closingBalance;
  Double automaticallyInvestedBalance;
  Double overdraftContractedLimit;
  Double overdraftUsedLimit;
  Double unarrangedOverdraftAmount;
  Boolean hasReservedBalance;
  List<ReservedBalance> reservedBalances;
}
