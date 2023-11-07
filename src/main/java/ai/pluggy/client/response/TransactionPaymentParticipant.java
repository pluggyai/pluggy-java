package ai.pluggy.client.response;

import lombok.Data;

@Data
public class TransactionPaymentParticipant {
  String name;
  String branchNumber;
  String accountNumber;
  String routingNumber;
  String routingNumberISPB;
  DocumentNumber documentNumber;
}
