package ai.pluggy.client.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Bacen's SCR (Sistema de Informações de Crédito) response, forwarded exactly as the Banco Central returns it: field names and codes are not renamed or normalised.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScrResponse {

  /** The base dates consulted (e.g. {@code 202604 a 202607}). */
  String dtbConsult;

  /** The consulted document: the CPF for an individual, or the 8-digit CNPJ root for a company. */
  String cdCli;

  /** Type of client: {@code "1"} for an individual, {@code "2"} for a legal entity. */
  String tpCli;

  /** One entry per consulted base date. A base date with no data is still listed, with no operations. */
  List<ScrDatabase> lsDtb;

  /** Validation messages raised by Bacen for this request. */
  List<ScrValidationMessage> listaDeMensagensDeValidacao;
}
