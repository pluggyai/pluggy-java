package ai.pluggy.client;

import ai.pluggy.client.response.Account;
import ai.pluggy.client.response.AccountsResponse;
import ai.pluggy.client.request.AccountsRequest;
import ai.pluggy.client.request.ConnectorsSearchRequest;
import ai.pluggy.client.request.CreateItemRequest;
import ai.pluggy.client.request.UpdateItemRequest;
import ai.pluggy.client.response.Connector;
import ai.pluggy.client.response.ConnectorsResponse;
import ai.pluggy.client.response.DeleteItemResponse;
import ai.pluggy.client.response.ItemResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface PluggyApiService {

  @GET("/connectors")
  Call<ConnectorsResponse> getConnectors();

  @GET("/connectors")
  Call<ConnectorsResponse> getConnectors(@QueryMap ConnectorsSearchRequest connectorsSearchRequest);

  @GET("/connectors/{id}")
  Call<Connector> getConnector(@Path("id") Integer connectorId);

  @POST("/items")
  Call<ItemResponse> createItem(@Body CreateItemRequest createItemRequest);

  @PATCH("/items/{id}")
  Call<ItemResponse> updateItem(@Path("id") String itemId,
    @Body UpdateItemRequest createItemRequest);

  @GET("/items/{id}")
  Call<ItemResponse> getItem(@Path("id") String itemId);

  @GET("/items/{id}")
  Call<DeleteItemResponse> deleteItem(@Path("id") String existingItemId);

  @GET("/accounts")
  Call<AccountsResponse> getAccounts(@Query("itemId") String itemId);

  @GET("/accounts")
  Call<AccountsResponse> getAccounts(@QueryMap AccountsRequest accountsRequest);

  @GET("/accounts/{id}")
  Call<Account> getAccount(@Path("id") String accountId);
}
