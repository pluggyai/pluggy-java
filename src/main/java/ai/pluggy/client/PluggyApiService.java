package ai.pluggy.client;

import ai.pluggy.client.request.ConnectorsSearchRequest;
import ai.pluggy.client.response.ConnectorsResponse;
import java.io.IOException;

public interface PluggyApiService {

  ConnectorsResponse getConnectors() throws IOException;
  ConnectorsResponse getConnectors(ConnectorsSearchRequest connectorsSearchRequest)
    throws IOException;
}
