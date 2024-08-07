package ai.pluggy.client.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
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
  CreditData creditData;
  BankData bankData;
}
