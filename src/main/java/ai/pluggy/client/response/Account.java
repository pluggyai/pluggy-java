package ai.pluggy.client.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
