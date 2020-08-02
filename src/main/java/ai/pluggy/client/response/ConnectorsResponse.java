package ai.pluggy.client.response;

import java.util.List;


/**
 * GET /connectors response entity
 */
public class ConnectorsResponse {

  private List<Connector> results;

  public List<Connector> getResults() {
    return results;
  }

  public void setResults(List<Connector> results) {
    this.results = results;
  }
}
