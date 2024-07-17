package ai.pluggy.client.response;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * GET /connectors connector.credentials[] item entity
 */
@Data
@AllArgsConstructor
@Builder
public class CredentialLabel {

  String name;
  String label;
  CredentialType type;
  Boolean mfa;
  @Builder.Default
  Boolean optional = false;
  String data;
  String assistiveText;
  String validation;
  String validationMessage;
  String placeholder;
  String instructions;
  Date expiresAt;
  CredentialSelectOption[] options;

  // override noargs constructor to explicitly set defaults
  public CredentialLabel() {
    this.optional = false;
  }
}
