package ai.pluggy.client.response;


import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class Account {

  String id;
  String type;
  String subtype;
  String name;
  String marketingName;
  String taxNumber;
  String owner;
  String number;
  Double balance;
  String itemId;
  String currencyCode;
  List<Transaction> transactions = new ArrayList<Transaction>();
  CreditData creditData;
  BankData bankData;
}
