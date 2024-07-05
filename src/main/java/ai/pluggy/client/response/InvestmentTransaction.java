package ai.pluggy.client.response;

import java.util.Date;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
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
