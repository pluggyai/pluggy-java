package ai.pluggy.client.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * GET /connectors response entity
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConnectorsResponse {

  List<Connector> results;
}
