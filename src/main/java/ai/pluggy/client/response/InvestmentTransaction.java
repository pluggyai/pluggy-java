package ai.pluggy.client.response;

import java.util.Date;
import lombok.Data;

@Data
public class InvestmentTransaction {

  InvestmentTransactionType type;
  Double quantity;
  Double value;
  Double amount;
  Date date;
  Date tradeDate;
  String isin;
}
