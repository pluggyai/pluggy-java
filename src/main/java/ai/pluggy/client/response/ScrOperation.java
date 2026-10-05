package ai.pluggy.client.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A group of SCR credit operations, aggregated by modality, source of funds, index and exchange variation.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScrOperation {

  /** Bacen's code for the operation modality. */
  String mod;

  /** Bacen's code for the source of funds. */
  String oriRec;

  /** Bacen's code for the reference rate or index. */
  String indx;

  /** Bacen's code for the exchange rate variation. */
  String varCamb;

  /** Present when the operation is under dispute: {@code D}, {@code J} or {@code JD}. */
  String subJDisc;

  /** Balance of the group split across Bacen's maturity vertices. */
  ScrMaturityBalances resVenc;

  /** Guarantees backing the operations in this group. */
  List<ScrGuarantee> lsGar;

  /** Complementary information reported for this group. */
  List<ScrAdditionalInfo> lsInfAd;
}
