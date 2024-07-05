package ai.pluggy.client.response;

import java.util.Date;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ItemResponse {

  String id;
  String user;
  Connector connector;
  Date createdAt;
  Date updatedAt;
  String status;
  ExecutionStatus executionStatus;
  Date lastUpdatedAt;
  String webhookUrl;
  ItemError error = null;
  CredentialLabel parameter;
  String clientUserId;
  Integer consecutiveFailedLoginAttempts;
}
