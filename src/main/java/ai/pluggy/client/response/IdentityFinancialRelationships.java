package ai.pluggy.client.response;

import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Information that allows institutions to assess the client's risk profile and economic-financial capacity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdentityFinancialRelationships {

  /** Date when the relationship with the institution started. */
  Date startDate;

  /** Products and services the client consumes. */
  List<String> productsServicesType;

  /** Additional info about the products and services, when {@code productsServicesType} includes {@code OUTROS}. */
  String productsServicesTypeAdditionalInfo;

  /** Procurators of the client. */
  List<IdentityProcurator> procurators;

  /** Accounts of the client with valid consent. Only accounts with explicit user consent are returned. */
  List<IdentityFinancialRelationshipAccount> accounts;

  /** Salary portabilities received from the client's previous paycheck banks (PF-only). */
  List<PortabilityReceived> portabilitiesReceived;

  /** Paycheck-bank links to employers, active or formerly active (PF-only). */
  List<PaycheckBankLink> paychecksBankLink;
}
