package ai.pluggy.client.response;

import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * GET /identity
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdentityResponse {

  String id;
  String fullName;
  String document;
  String taxNumber;
  String documentType;
  String jobTitle;
  InvestorProfile investorProfile;
  Date birthDate;
  List<Address> addresses;
  List<PhoneNumber> phoneNumbers;
  List<Email> emails;
  /**
   * @deprecated the API sends this list as {@code relations}, so this field is never filled. Use
   * {@link #getRelations()}.
   */
  @Deprecated
  List<IdentityRelation> identityRelations;
  Date createdAt;
  Date updatedAt;
  String companyName;
  /** Social name of the natural person (PF-only). */
  String socialName;
  /** Sex of the natural person (PF-only). */
  Sex sex;
  /** Marital status of the natural person (PF-only). */
  MaritalStatus maritalStatus;
  /** Nationality of the natural person (PF-only). */
  Nationality nationality;
  /** Other identification documents the natural person holds (PF-only). */
  List<OtherDocument> otherDocuments;
  /** Passport metadata for the natural person (PF-only). */
  Passport passport;
  /** Date the business was incorporated (PJ-only). */
  Date incorporationDate;
  /** Partners and administrators of the business (PJ-only). */
  List<BusinessParty> parties;
  /**
   * Additional documents for businesses headquartered abroad and not required to register a CNPJ
   * (PJ-only).
   */
  List<BusinessOtherDocument> businessOtherDocuments;
  /** CNPJs of the financial institutions responsible for the customer cadastro. */
  List<String> companiesCnpj;
  /** Establishment code (only for PAYMENT_ACCOUNT connectors). */
  String establishmentCode;
  /** Name of the establishment (only for PAYMENT_ACCOUNT connectors). */
  String establishmentName;
  /** Names related to the account owner (mother, father, spouse). */
  List<IdentityRelation> relations;
  /**
   * Information to assess the client's risk profile and economic-financial capacity: relationship
   * start date, products consumed, procurators, consented accounts and paycheck-bank links.
   */
  IdentityFinancialRelationships financialRelationships;
  /** Occupation, informed income/patrimony and, for businesses, economic activities and revenue. */
  IdentityQualifications qualifications;
}
