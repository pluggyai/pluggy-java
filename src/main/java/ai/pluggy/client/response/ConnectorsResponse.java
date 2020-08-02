package ai.pluggy.client.response;

import java.util.List;
import lombok.Data;

/**
 * GET /connectors response entity
 */
@Data
public class ConnectorsResponse {

  List<Connector> results;
}
