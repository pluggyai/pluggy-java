package ai.pluggy.client.response;

import java.util.List;
import lombok.Data;

@Data
public class Connector {

  Integer id;
  String name;
  String primaryColor;
  String institutionUrl;
  String country;
  String type;
  List<CredentialLabel> credentials;
  String imageUrl;
  Boolean hasMFA;
}
