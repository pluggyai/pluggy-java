package ai.pluggy.client.response;

import java.util.Date;
import lombok.Data;

@Data
public class ItemResponse {

  String id;
  String user;
  Connector connector;
  Date createdAt;
  Date updatedAt;
  String status;
  String executionStatus;
  Date lastUpdatedAt;
  String webhookUrl;
  ItemError error = null;
}
