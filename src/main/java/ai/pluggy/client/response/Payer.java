package ai.pluggy.client.response;

import lombok.Data;

@Data
public class Payer {
  
  String name;
  String branchNumber;
  String accountNumber;
  String routingNumber;
  DocumentNumber documentNumber;
}
