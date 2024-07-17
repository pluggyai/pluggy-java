package ai.pluggy.client.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class CredentialSelectOption {

  String label;
  String value;
}
