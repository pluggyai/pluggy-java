package ai.pluggy.client.response;

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
  String validation;
  String validationMessage;
  String placeholder;
  @Builder.Default
  Boolean optional = false;

  // override noargs constructor to explicitly set defaults
  public CredentialLabel() {
    this.optional = false;
  }
}
