package ai.pluggy.client.response;

import java.util.Date;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
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
}