package ai.pluggy.client.response;

import java.util.Date;
import java.util.List;
import lombok.Data;

@Data
public class Connector {
  Integer id;
  String name;
  String primaryColor;
  String institutionUrl;
  String country;
  ConnectorType type;
  List<CredentialLabel> credentials;
  String resetPasswordUrl;
  Boolean oauth;
  String imageUrl;
  Boolean hasMFA;
  Date createdAt;
}
