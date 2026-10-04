package ai.pluggy.client.response;

import lombok.Data;

import java.util.Date;

@Data
public class TransactionCreditCardMetadata {
    Integer installmentNumber;
    Integer totalInstallments;
    Double totalAmount;
    Integer payeeMCC;
    Date purchaseDate;
    String cardNumber;
    String billId;
    CreditCardAccountFeeType feeType;
    String feeTypeAdditionalInfo;
    CreditCardAccountOtherCreditType otherCreditsType;
    String otherCreditsAdditionalInfo;
    String billForecastDate;
    /**
     * Whether the transaction was a single charge or an installment.
     * Only returned for Open Finance connectors; not retroactive.
     */
    CreditCardAccountPaymentType paymentType;
    /**
     * Date the institution posted the transaction to a bill, in 'YYYY-MM-DD' format, as reported
     * by the institution. Null when the transaction was not posted to a bill yet or the institution
     * does not report it. Kept as a String to avoid timezone shifts on a date-only value.
     * Only returned for Open Finance connectors; not retroactive.
     */
    String billPostDate;
    /**
     * ISO-8601 timestamp of the transaction, as reported by the institution.
     * Only returned for Open Finance connectors; not retroactive.
     */
    String transactionDateTime;
}
