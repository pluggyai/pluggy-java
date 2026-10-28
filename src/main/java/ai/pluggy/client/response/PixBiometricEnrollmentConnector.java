package ai.pluggy.client.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Institution where a Pix por biometria linked account is held. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PixBiometricEnrollmentConnector {

  /** Connector primary identifier. */
  Integer id;

  /** Institution name. */
  String name;

  /** Institution logo. */
  String imageUrl;
}
