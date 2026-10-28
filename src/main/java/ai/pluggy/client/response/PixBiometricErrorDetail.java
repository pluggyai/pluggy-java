package ai.pluggy.client.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Reason the institution gave when a Pix por biometria link was rejected. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PixBiometricErrorDetail {

  String code;

  String description;
}
