package ai.pluggy.client.response;

import java.util.Date;
import lombok.Data;

@Data
public class ItemResponse {

  String id;
  String user;
  Integer userId;
  Connector connector;
  Integer connectorId;
  Date createdAt;
  Date updatedAt;
  String status;
  String executionStatus;
  Date lastUpdatedAt;
  String webhookUrl;
  ErrorResponse error = null;
}
