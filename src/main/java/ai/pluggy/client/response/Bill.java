package ai.pluggy.client.response;

import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@Builder
public class Bill {

  String id;
  Date dueDate;
  Double totalAmount;
  String totalAmountCurrencyCode;
  Double minimumPaymentAmount;
  boolean allowsInstallments;
  List<FinancialCharge> financeCharges;
  Date createdAt;
  Date updatedAt;
}
