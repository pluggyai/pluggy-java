package ai.pluggy.client.response;

import lombok.Data;

import java.util.Date;

@Data
public class TransactionCreditCardMetadata {
    Integer installmentNumber;
    Integer totalInstallments;
    Double totalAmount;
    Integer payeeMcc;
    Date purchaseDate;
    String cardNumber;
    String billId;
}
