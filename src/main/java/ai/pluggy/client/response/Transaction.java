package ai.pluggy.client.response;

import lombok.Data;

@Data
public class Transaction {
  String id;
  String accountId;
  String description;
  String descriptionRaw;
  String currencyCode;
  Double amount;
  Double amountInAccountCurrency;
  String date;
  Double balance;
  String category;
  String categoryId;
  String providerCode;
  TransactionStatus status;
  PaymentData paymentData;
  TransactionCreditCardMetadata creditCardMetadata;
  TransactionType type;
  Merchant merchant;
  String operationType;
  String providerId;
}
