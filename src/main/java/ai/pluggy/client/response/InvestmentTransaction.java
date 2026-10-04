package ai.pluggy.client.response;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvestmentTransaction {
  String id;
  InvestmentTransactionType type;
  String description;
  Double quantity;
  Double value;
  Double amount;
  String brokerageNumber;
  String netAmount;
  Expenses expenses;
  Date date;
  Date tradeDate;
}
