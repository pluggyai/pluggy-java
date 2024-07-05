package ai.pluggy.client.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InvestmentMetadata {
  String taxRegime;
  String proposalNumber;
  String processNumber;
}
