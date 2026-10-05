package ai.pluggy.client.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Legal representative or procurator of the client.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdentityProcurator {

  /** Type of relationship: {@code REPRESENTANTE_LEGAL} or {@code PROCURADOR}. */
  String type;

  /** CPF of the procurator. For business links it may hold a CPF or a CNPJ; prefer {@link #documentNumber}. */
  String cpfNumber;

  /** Document number of the procurator (CPF or CNPJ). */
  String documentNumber;

  /** Type of document carried by {@link #documentNumber}: {@code CPF} or {@code CNPJ}. */
  String documentType;

  /** Civil name of the procurator (company name for business procurators). */
  String civilName;

  /** Social name of the procurator, if any. */
  String socialName;
}
