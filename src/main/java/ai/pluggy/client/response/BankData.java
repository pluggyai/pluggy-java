package ai.pluggy.client.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
