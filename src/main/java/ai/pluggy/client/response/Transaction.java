package ai.pluggy.client.response;
import java.util.Date;


import lombok.Data;

@Data
public class Transaction {
  String id;
  String accountId;
  String description;
  String descriptionRaw;
  String currencyCode;
  Double amount;
  Date date;
  Double balance;
  String category;
  String providerCode;
  TransactionStatus status;
  PaymentData paymentData;
  TransactionCreditCardMetadata creditCardMetadata;
  TransactionType type;
}
