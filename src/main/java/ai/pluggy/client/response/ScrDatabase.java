package ai.pluggy.client.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * One consulted SCR base date, and what the SCR holds for the client in it.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScrDatabase {

  /** The base date, as YYYYMM (numeric). */
  Integer dtb;

  /** Bacen's message for this base date, when it has one. */
  String msg;

  /** Percentage of the expected 3040 documents already incorporated by Bacen for this base date. */
  String docProc;

  /** Percentage of the expected operation volume already accepted for this base date. */
  String volProc;

  /** Number of financial institutions where the client has operations. */
  Integer qtdIfs;

  /** Number of financial conglomerates where the client has operations. */
  Integer qtdCongFinc;

  /** Start of the client's relationship with the national financial system. */
  String dtbIniRel;

  /** Co-obligation assumed by the client in credit assignments, in BRL. */
  Double coobAss;

  /** Co-obligation received in credit assignments, in BRL. */
  Double coobRec;

  /** Operation groups reported for this base date. */
  List<ScrOperation> lsOp;
}
