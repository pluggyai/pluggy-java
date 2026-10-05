package ai.pluggy.client.response;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Patrimony informed by the client.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InformedPatrimony {

  /** Amount of the informed patrimony. */
  Double amount;

  /** Year of the patrimony. */
  Integer year;

  /** Reference date of the patrimony (business path only). */
  Date date;
}
