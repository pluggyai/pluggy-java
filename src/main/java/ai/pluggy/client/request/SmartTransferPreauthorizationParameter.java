package ai.pluggy.client.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

/** Identifies the payer of a Smart Transfer preauthorization. */
@Value
@AllArgsConstructor
@Builder
public class SmartTransferPreauthorizationParameter {

  /** CPF of the payer. Required. */
  String cpf;

  /** CNPJ of the payer, for business accounts. */
  String cnpj;

  public SmartTransferPreauthorizationParameter(String cpf) {
    this.cpf = cpf;
    this.cnpj = null;
  }
}
