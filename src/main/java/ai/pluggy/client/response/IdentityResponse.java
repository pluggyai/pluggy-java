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
}
