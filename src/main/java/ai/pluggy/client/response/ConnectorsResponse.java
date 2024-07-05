package ai.pluggy.client.response;

import java.util.List;

import lombok.Builder;
import lombok.Data;

/**
 * GET /connectors response entity
 */
@Data
@Builder
public class ConnectorsResponse {

  List<Connector> results;
}
