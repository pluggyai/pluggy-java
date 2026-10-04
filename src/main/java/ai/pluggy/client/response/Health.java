package ai.pluggy.client.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Health {
  ConnectorStatus status;

  String stage;

  /**
   * Incidents affecting this connector right now, worst first. Null when there
   * are none, so its presence is the signal.
   *
   * <p>It describes the institution, not your own connections. {@link #status}
   * answers "can I connect at all"; this answers "what is wrong" — a bank can
   * be perfectly reachable and still be failing to return instalments.
   */
  List<ConnectorIncident> incidents;
}
