package ai.pluggy.client.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Transactional limits per period of a Smart Transfer preauthorization. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SmartTransferPeriodicLimits {

  SmartTransferPeriodicLimit day;
  SmartTransferPeriodicLimit week;
  SmartTransferPeriodicLimit month;
  SmartTransferPeriodicLimit year;
}
