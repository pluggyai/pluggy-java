package ai.pluggy.client.response;

import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Smart Transfer preauthorization: the payer's consent to send recurring transfers to a set of
 * recipients without approving each one at the bank.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SmartTransferPreauthorization {

  String id;

  SmartTransferPreauthorizationStatus status;

  /** Url where the payer gives the consent at the institution. */
  String consentUrl;

  String clientPreauthorizationId;

  SmartTransferCallbackUrls callbackUrls;

  List<PaymentRecipient> recipients;

  Connector connector;

  Date createdAt;

  Date updatedAt;

  SmartTransferPreauthorizationConfiguration configuration;

  /** Set when {@code status} is {@code ERROR}. */
  SmartTransferErrorDetail errorDetail;

  /**
   * Permission to read the source account balance, requested with {@code linkedJourney}; null
   * when it was not requested.
   */
  SmartTransferDataConsent dataConsent;
}
