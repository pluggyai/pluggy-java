package ai.pluggy.client.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReservedBalanceRemuneration {

  Double preFixedRate;
  Double postFixedIndexerPercentage;
  String rateType;
  String indexer;
  String calculation;
  String ratePeriodicity;
  String indexerAdditionalInfo;
}
