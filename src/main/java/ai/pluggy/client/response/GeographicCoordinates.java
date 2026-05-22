package ai.pluggy.client.response;

import lombok.Builder;
import lombok.Data;

/**
 * Geographic coordinates in decimal degrees, WGS84 reference system.
 */
@Data
@Builder
public class GeographicCoordinates {

  /** Between -90 and 90. */
  Double latitude;
  /** Between -180 and 180. */
  Double longitude;
}
