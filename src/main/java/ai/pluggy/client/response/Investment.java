package ai.pluggy.client.response;

import lombok.Data;

@Data
public class Investment {

  String id;
  String number;
  String name;
  Double balance;
  String currencyCode;
  String type;
  Double annualRate;
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
}
