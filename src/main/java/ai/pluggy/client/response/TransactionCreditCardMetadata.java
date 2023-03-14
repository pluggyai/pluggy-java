package ai.pluggy.client.response;

import lombok.Data;

@Data
public class TransactionCreditCardMetadata {
    Integer installmentNumber;
    Integer totalInstallments;
    Double totalAmount;
}
