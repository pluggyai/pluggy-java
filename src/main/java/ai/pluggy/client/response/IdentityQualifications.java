package ai.pluggy.client.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Qualification data of the client: occupation, informed income/patrimony and, for businesses, economic activities and revenue.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdentityQualifications {

  /** CNPJ of the company. */
  String companyCnpj;

  /** Occupation code: {@code RECEITA_FEDERAL}, {@code CBO} or {@code OUTRO}. */
  String occupationCode;

  /** Occupation description (list code for {@code RECEITA_FEDERAL}/{@code CBO}, free text for {@code OUTRO}). */
  String occupationDescription;

  /** Informed income. */
  InformedIncome informedIncome;

  /** Informed patrimony. */
  InformedPatrimony informedPatrimony;

  /** CNAE codes describing the business's economic activities. */
  List<EconomicActivity> economicActivities;

  /** Revenue informed by the business. */
  InformedRevenue informedRevenue;
}
