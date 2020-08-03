package ai.pluggy.client.response;

import lombok.Data;

@Data
public class Transaction {

  String id;
  String description;
  String currencyCode;
  Double amount;
  String date;
  Double balance;
  String category;
}
