package ai.pluggy.client.response;

import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemProductState {
  /** Whether product was collected in this last execution or not */
  boolean isUpdated;
  /** Date when product was last collected for this Item, null if it has never been. */
  Date lastUpdatedAt;
  /** If product was not collected, this field will provide more detailed info about the reason. */
  List<ItemProductStepWarning> warnings;
}
