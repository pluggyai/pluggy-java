package ai.pluggy.client.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Balance of an SCR operation group split across Bacen's maturity vertices, in BRL. Each field is a vertex code as defined by Bacen's DOC3040 reference; only the vertices that carry a value are present (others are null).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScrMaturityBalances {

  Double v20;

  Double v40;

  Double v60;

  Double v80;

  Double v110;

  Double v120;

  Double v130;

  Double v140;

  Double v150;

  Double v160;

  Double v165;

  Double v170;

  Double v175;

  Double v180;

  Double v190;

  Double v199;

  Double v205;

  Double v210;

  Double v220;

  Double v230;

  Double v240;

  Double v245;

  Double v250;

  Double v255;

  Double v260;

  Double v270;

  Double v280;

  Double v290;

  Double v310;

  Double v320;
}
