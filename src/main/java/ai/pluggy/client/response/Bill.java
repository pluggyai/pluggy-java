package ai.pluggy.client.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Bill {

  String id;
  Date dueDate;
  Date billClosingDate;
  Double totalAmount;
  String totalAmountCurrencyCode;
  Double minimumPaymentAmount;
  boolean allowsInstallments;
  List<FinancialCharge> financeCharges;
  Date createdAt;
  Date updatedAt;
}
