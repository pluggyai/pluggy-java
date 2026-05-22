package ai.pluggy.client.response;

import java.util.Date;
import java.util.List;

import lombok.Builder;
import lombok.Data;

/**
 * GET /identity
 */
@Data
@Builder
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
}
