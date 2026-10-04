package ai.pluggy.client.response;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemResponse {

  String id;
  String user;
  Connector connector;
  Date createdAt;
  Date updatedAt;
  ItemStatus status;
  ExecutionStatus executionStatus;
  Date lastUpdatedAt;
  String webhookUrl;
  ItemError error = null;
  CredentialLabel parameter;
  ItemStatusDetail statusDetail;
  String clientUserId;
  Integer consecutiveFailedLoginAttempts;

  /**
   * Open Finance only. When the financial institution's resource list was last read for this
   * item, or null if it never was. An empty {@code GET /items/{id}/resources} page means the
   * institution shared nothing only when this is set.
   */
  Date resourcesCollectedAt;

  /**
   * Open Finance only. Whether the financial institution declares at least one of this item's
   * resources {@code PENDING_AUTHORISATION}: the user still has to approve it at their bank. False
   * when the resource list was read and none is; null for connectors other than Open Finance, and
   * while the resource list has not been read yet ({@code resourcesCollectedAt} is null).
   */
  Boolean hasResourcesPendingAuthorization;
}
