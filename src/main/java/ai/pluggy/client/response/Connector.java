package ai.pluggy.client.response;

import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Connector {
  Integer id;
  String name;
  String primaryColor;
  String institutionUrl;
  String country;
  ConnectorType type;
  List<CredentialLabel> credentials;
  List<ProductType> products;
  Boolean oauthUrl;
  String resetPasswordUrl;
  Boolean oauth;
  String imageUrl;
  Boolean hasMFA;
  Health health;
  Date createdAt;
  Boolean isOpenFinance;
  Date updatedAt;
  Boolean supportsPaymentInitiation;
  Boolean isSandbox;
  Boolean supportsScheduledPayments;
  Boolean supportsSmartTransfers;
  Boolean supportsBoletoManagement;
  /** Whether the connector supports automatic Pix. */
  Boolean supportsAutomaticPix;
  /**
   * Which sub-products the institution serves, in the Open Finance directory's vocabulary (e.g.
   * {@code INVESTMENTS:TREASURE_TITLES}). Absent for direct connectors.
   */
  List<String> productCoverage;
}