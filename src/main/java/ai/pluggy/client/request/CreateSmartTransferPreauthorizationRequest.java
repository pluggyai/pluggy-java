package ai.pluggy.client.request;

import ai.pluggy.client.response.SmartTransferCallbackUrls;
import ai.pluggy.client.response.SmartTransferPreauthorizationConfiguration;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

/** POST /smart-transfers/preauthorizations request body. */
@Value
@AllArgsConstructor
@Builder
public class CreateSmartTransferPreauthorizationRequest {

  /** Connector of the payer's institution. Required. */
  Integer connectorId;

  /** Payer identification. Required. */
  SmartTransferPreauthorizationParameter parameters;

  /** Ids of the payment recipients the preauthorization allows transfers to. Required. */
  List<String> recipientIds;

  SmartTransferCallbackUrls callbackUrls;

  String clientPreauthorizationId;

  SmartTransferPreauthorizationConfiguration configuration;

  /**
   * When true, the user is also asked, in the same approval at the bank, for a permission to read
   * the source account balance. Check {@code dataConsent} on the preauthorization and read the
   * balance once it is {@code AUTHORISED}. Omitted from the request when null (the API defaults it
   * to false).
   */
  Boolean linkedJourney;

  public CreateSmartTransferPreauthorizationRequest(Integer connectorId,
      SmartTransferPreauthorizationParameter parameters, List<String> recipientIds) {
    this.connectorId = connectorId;
    this.parameters = parameters;
    this.recipientIds = recipientIds;
    this.callbackUrls = null;
    this.clientPreauthorizationId = null;
    this.configuration = null;
    this.linkedJourney = null;
  }
}
