package ai.pluggy.client.response;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * What is broken, as opposed to how badly ({@code severity}) or how far along
 * Pluggy is ({@code state}).
 *
 * <p>Use it to group the same problem across institutions, and to decide which
 * incidents are worth showing your own users: a {@code SCHEDULED_MAINTENANCE}
 * and a {@code TRANSACTIONS_MISSING} both read as "degraded" otherwise.
 *
 * <p>{@code OTHER} means Pluggy has not classified the incident, not that
 * nothing is wrong. A value added server-side after this SDK release
 * deserializes as {@code null}.
 */
@AllArgsConstructor
public enum ConnectorIncidentType {

  // Whether you can connect at all.
  @SerializedName("CONNECTOR_UNAVAILABLE")
  CONNECTOR_UNAVAILABLE("CONNECTOR_UNAVAILABLE"),

  @SerializedName("CONNECTOR_DEGRADED")
  CONNECTOR_DEGRADED("CONNECTOR_DEGRADED"),

  @SerializedName("INSTITUTION_OUTAGE")
  INSTITUTION_OUTAGE("INSTITUTION_OUTAGE"),

  @SerializedName("SCHEDULED_MAINTENANCE")
  SCHEDULED_MAINTENANCE("SCHEDULED_MAINTENANCE"),

  // It answers, but the connection does not complete or does not refresh.
  @SerializedName("CONSENT_ERROR")
  CONSENT_ERROR("CONSENT_ERROR"),

  @SerializedName("CONNECTION_NOT_UPDATING")
  CONNECTION_NOT_UPDATING("CONNECTION_NOT_UPDATING"),

  @SerializedName("PARTIAL_SUCCESS")
  PARTIAL_SUCCESS("PARTIAL_SUCCESS"),

  // It connects and syncs, but what comes back is wrong or incomplete.
  @SerializedName("ACCOUNTS_MISSING")
  ACCOUNTS_MISSING("ACCOUNTS_MISSING"),

  @SerializedName("BALANCE_INCORRECT")
  BALANCE_INCORRECT("BALANCE_INCORRECT"),

  @SerializedName("TRANSACTIONS_MISSING")
  TRANSACTIONS_MISSING("TRANSACTIONS_MISSING"),

  @SerializedName("TRANSACTIONS_INCORRECT")
  TRANSACTIONS_INCORRECT("TRANSACTIONS_INCORRECT"),

  @SerializedName("TRANSACTIONS_INSTALLMENTS_ISSUE")
  TRANSACTIONS_INSTALLMENTS_ISSUE("TRANSACTIONS_INSTALLMENTS_ISSUE"),

  @SerializedName("INVESTMENTS_MISSING")
  INVESTMENTS_MISSING("INVESTMENTS_MISSING"),

  @SerializedName("INVESTMENTS_INCORRECT")
  INVESTMENTS_INCORRECT("INVESTMENTS_INCORRECT"),

  @SerializedName("IDENTITY_MISSING")
  IDENTITY_MISSING("IDENTITY_MISSING"),

  @SerializedName("HISTORICAL_DATA_MISSING")
  HISTORICAL_DATA_MISSING("HISTORICAL_DATA_MISSING"),

  // Pluggy's own platform, not the institution's.
  @SerializedName("WEBHOOK_DELAY")
  WEBHOOK_DELAY("WEBHOOK_DELAY"),

  @SerializedName("PAYMENT_FAILURE")
  PAYMENT_FAILURE("PAYMENT_FAILURE"),

  @SerializedName("OTHER")
  OTHER("OTHER");

  @Getter
  private String value;
}
