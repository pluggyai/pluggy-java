package ai.pluggy.client.response;

import java.util.Date;

import lombok.Builder;
import lombok.Data;

/**
 * Partner or administrator of a business.
 *
 * Partners with less than 25% shareholding may be omitted by the institution.
 */
@Data
@Builder
public class BusinessParty {

  BusinessPartyType type;
  BusinessPartyPersonType personType;
  BusinessPartyDocumentType documentType;
  String documentNumber;
  /** Issuing country of the document, alpha3 ISO-3166. */
  String documentCountry;
  Date documentExpirationDate;
  Date documentIssueDate;
  String documentAdditionalInfo;
  /** Civil name. Required when personType is NATURAL_PERSON. */
  String civilName;
  String socialName;
  /** Company name. Required when personType is LEGAL_ENTITY. */
  String companyName;
  String tradeName;
  Date startDate;
  /**
   * Shareholding fraction between 0 and 1 (e.g. 0.51 represents 51%, 1 represents 100%).
   * Required when type is PARTNER and the shareholding is 25% or higher.
   */
  Double shareholding;
}
