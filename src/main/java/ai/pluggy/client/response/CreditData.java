package ai.pluggy.client.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreditData {

  String level;
  String brand;
  String balanceCloseDate;
  String balanceDueDate;
  Double availableCreditLimit;
  Double balanceForeignCurrency;
  Double minimumPayment;
  Double creditLimit;
}
