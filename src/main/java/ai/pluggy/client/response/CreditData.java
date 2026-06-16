package ai.pluggy.client.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CreditData {

  String level;
  String brand;
  String brandAdditionalInfo;
  String balanceCloseDate;
  String balanceDueDate;
  Double availableCreditLimit;
  Double balanceForeignCurrency;
  Double minimumPayment;
  Double creditLimit;
  HolderType holderType;
  CreditCardStatus status;
  List<DisaggregatedCreditLimit> disaggregatedCreditLimits;
}
