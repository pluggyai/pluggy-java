package ai.pluggy.client.response;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * An incident affecting a connector right now, as published on
 * https://status.pluggy.ai.
 *
 * <p>Only incidents active at this moment are listed: a scheduled maintenance
 * appears once its window opens, not when it is announced.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConnectorIncident {
  String id;

  /** Short, customer-facing summary. Safe to show to your own users. */
  String title;

  String description;

  ConnectorIncidentType type;

  /**
   * Affected product line: {@code dados}, {@code pis}, {@code pis-agendado},
   * {@code pixauto}, {@code smart} or {@code infra}.
   */
  String product;

  /** {@code INCIDENT} for an unplanned problem, {@code MAINTENANCE} for a planned window. */
  String kind;

  /** {@code DEGRADED}, {@code PARTIAL_OUTAGE}, {@code MAJOR_OUTAGE} or {@code MAINTENANCE}. */
  String severity;

  /**
   * {@code INVESTIGATING}, {@code IDENTIFIED}, {@code MONITORING} or
   * {@code SCHEDULED}. Resolved incidents are not listed.
   */
  String state;

  Date startedAt;

  Date updatedAt;

  /** Permalink to the full timeline and postmortem on the status page. */
  String url;
}
