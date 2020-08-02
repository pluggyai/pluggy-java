package ai.pluggy.client;

import ai.pluggy.client.request.ConnectorsSearchRequest;
import ai.pluggy.client.response.ConnectorsResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

public interface PluggyApiService {

  @GET("/connectors")
  Call<ConnectorsResponse> getConnectors();

  @GET("/connectors")
  Call<ConnectorsResponse> getConnectors(@QueryMap ConnectorsSearchRequest connectorsSearchRequest);
}
