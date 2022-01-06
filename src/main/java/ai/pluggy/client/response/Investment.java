package ai.pluggy.client.response;

import java.util.Date;
import lombok.Data;

@Data
public class Investment {

  String id;
  String number;
  String name;
  Double balance;
  String currencyCode;
  InvestmentType type;
  InvestmentSubtype subtype;
  Double annualRate;
  Double lastTwelveMonthsRate;
  String itemId;
  String code;
  Double value;
  Double quantity;
  Double amount;
  Double taxes;
  Double taxes2;
  String date;
  String owner;
  String amountProfit = null;
  Double amountWithdrawal;
  InvestmentTransaction[] transactions;
  Date dueDate;
  String issuer;
  Date issuerDate;
  Double rate;
  String rateType;
  Double originalAmount;
  Double lastMonthRate;
  InvestmentStatus status;
  String isin;
  
}
