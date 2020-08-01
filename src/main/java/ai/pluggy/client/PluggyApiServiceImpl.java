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

  private String baseUrl;
  private String getConnectorsUrlPath = "/connectors";

  private PluggyClient pluggyClient;

  public PluggyApiServiceImpl(PluggyClient pluggyClient, String baseUrl) {
    this.pluggyClient = pluggyClient;
    this.baseUrl = baseUrl;
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
    pluggyClient.ensureAuthenticated();

    String queryString = formatQueryParams(connectorSearch);
    String urlString = this.baseUrl + this.getConnectorsUrlPath + queryString;

    Request request = new Request.Builder()
      .url(urlString)
      .addHeader("content-type", "application/json")
      .addHeader("x-api-key", pluggyClient.getApiKey())
      .build();

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
