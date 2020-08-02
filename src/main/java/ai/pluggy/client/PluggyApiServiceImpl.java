package ai.pluggy.client;

import static ai.pluggy.utils.Utils.formatQueryParams;

import ai.pluggy.client.request.ConnectorsSearchRequest;
import ai.pluggy.client.response.ConnectorsResponse;
import ai.pluggy.exception.PluggyException;
import ai.pluggy.utils.Asserts;
import com.google.gson.Gson;
import java.io.IOException;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class PluggyApiServiceImpl implements PluggyApiService {

  private String getConnectorsUrlPath = "/connectors";

  private PluggyClient pluggyClient;

  public PluggyApiServiceImpl(PluggyClient pluggyClient) {
    this.pluggyClient = pluggyClient;
  }

  private Request.Builder baseRequestBuilder(String pathString) {
    String url = this.pluggyClient.getBaseUrl() + (pathString != null ? pathString : "");
    return new Request.Builder()
      .url(url)
      .addHeader("content-type", "application/json");
  }

  private Request.Builder baseRequestBuilder() {
    return baseRequestBuilder(null);
  }

  /**
   * GET /connectors request - retrieve all results
   *
   * @return connectorsResponse
   */
  @Override
  public ConnectorsResponse getConnectors() throws IOException {
    return getConnectors(new ConnectorsSearchRequest());
  }

  /**
   * GET /connectors request with search params
   *
   * @param connectorSearch - search params such as "name", "countries" and "types"
   * @return connectorsResponse
   */
  @Override
  public ConnectorsResponse getConnectors(ConnectorsSearchRequest connectorSearch)
    throws IOException {

    String queryString = formatQueryParams(connectorSearch);
    Request request = baseRequestBuilder(this.getConnectorsUrlPath + queryString).build();

    ConnectorsResponse connectorsResponse;

    try (Response response = pluggyClient.getHttpClient().newCall(request).execute()) {
      if (!response.isSuccessful()) {
        throw new PluggyException("Pluggy GET connectors request failed", response);
      }
      ResponseBody responseBody = response.body();
      Asserts.assertNotNull(responseBody, "response.body()");

      connectorsResponse = new Gson().fromJson(responseBody.string(), ConnectorsResponse.class);
    }

    return connectorsResponse;
  }
}
