package ai.pluggy.client;

import ai.pluggy.client.request.AccountsRequest;
import ai.pluggy.client.request.ConnectorsSearchRequest;
import ai.pluggy.client.request.CreateConnectTokenRequest;
import ai.pluggy.client.request.CreateItemRequest;
import ai.pluggy.client.request.InvestmentTransactionsSearchRequest;
import ai.pluggy.client.request.ItemResourcesSearchRequest;
import ai.pluggy.client.request.ItemsCursorSearchRequest;
import ai.pluggy.client.request.TransactionsCursorSearchRequest;
import ai.pluggy.client.request.TransactionsSearchRequest;
import ai.pluggy.client.request.UpdateItemMfaRequest;
import ai.pluggy.client.request.UpdateItemRequest;
import ai.pluggy.client.response.*;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
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

  @GET("/identity")
  Call<IdentityResponse> getIdentityByItemId(@Query("itemId") String itemId);

  @GET("/identity/{id}")
  Call<IdentityResponse> getIdentityById(@Path("id") String identityId);

  @POST("/items")
  Call<ItemResponse> createItem(@Body CreateItemRequest createItemRequest);

  @PATCH("/items/{id}")
  Call<ItemResponse> updateItem(@Path("id") String itemId);

  @PATCH("/items/{id}")
  Call<ItemResponse> updateItem(@Path("id") String itemId,
      @Body UpdateItemRequest updateItemRequest);

  @POST("/items/{id}/mfa")
  Call<ItemResponse> updateItemSendMfa(@Path("id") String itemId,
      @Body UpdateItemMfaRequest mfaParameter);

  @GET("/items/{id}")
  Call<ItemResponse> getItem(@Path("id") String itemId);

  @DELETE("/items/{id}")
  Call<DeleteItemResponse> deleteItem(@Path("id") String existingItemId);

  /**
   * First page of the team's items, newest first, cursor-paginated. Fetch the following pages by
   * passing {@link ItemsCursorResponse#getNextCursor()} as
   * {@link ItemsCursorSearchRequest#after(String)} until it returns null.
   *
   * <p><b>Availability: opt-in, paid plans only.</b> Listing items is disabled by default and is
   * only available to paid-plan teams that have explicitly requested it from Pluggy support. Teams
   * without it enabled get {@code 403 LIST_ITEMS_FEATURE_NOT_ENABLED}. For most integrations,
   * store each itemId when it is created (Pluggy Connect {@code onSuccess} or the
   * {@code item/created} webhook) and use {@link #getItem(String)} instead.
   */
  @GET("/v2/items")
  Call<ItemsCursorResponse> getItems();

  /**
   * Items filtered by {@code clientUserId} and/or {@code connectorId}, newest first,
   * cursor-paginated. See {@link #getItems()}.
   *
   * <p><b>Availability: opt-in, paid plans only.</b> Listing items is disabled by default and is
   * only available to paid-plan teams that have explicitly requested it from Pluggy support. Teams
   * without it enabled get {@code 403 LIST_ITEMS_FEATURE_NOT_ENABLED}. For most integrations,
   * store each itemId when it is created (Pluggy Connect {@code onSuccess} or the
   * {@code item/created} webhook) and use {@link #getItem(String)} instead.
   */
  @GET("/v2/items")
  Call<ItemsCursorResponse> getItems(@QueryMap ItemsCursorSearchRequest itemsCursorSearchRequest);

  /**
   * Open Finance only: the resources the financial institution declared for the item's consent.
   * Items on other connectors return an empty page.
   */
  @GET("/items/{id}/resources")
  Call<ItemResourcesResponse> getItemResources(@Path("id") String itemId);

  @GET("/items/{id}/resources")
  Call<ItemResourcesResponse> getItemResources(@Path("id") String itemId,
      @QueryMap ItemResourcesSearchRequest itemResourcesSearchRequest);

  /**
   * Retrieve the SCR (Bacen's Sistema de Informações de Crédito) for the document behind an item,
   * for the last 4 available base dates. The response is Bacen's own payload, forwarded unchanged.
   *
   * <p>Opt-in: requires the SCR feature to be enabled for your team (ask Pluggy support); otherwise
   * 403 SCR_FEATURE_NOT_ENABLED (see {@link ErrorResponse#getCodeDescription()}). Only available for
   * Open Finance items with a known CPF/CNPJ (otherwise 422 SCR_ITEM_NOT_SUPPORTED).
   *
   * @param itemId item primary identifier
   */
  @GET("/items/{id}/scr")
  Call<ScrResponse> getItemScr(@Path("id") String itemId);

  /**
   * Retrieve the SCR (Bacen's Sistema de Informações de Crédito) for the document behind an item,
   * for a range of base dates. Base dates are months, and Bacen consolidates each one with a few
   * months of delay, so the current month returns nothing.
   *
   * <p>Opt-in: requires the SCR feature to be enabled for your team (ask Pluggy support); otherwise
   * 403 SCR_FEATURE_NOT_ENABLED (see {@link ErrorResponse#getCodeDescription()}). Only available for
   * Open Finance items with a known CPF/CNPJ (otherwise 422 SCR_ITEM_NOT_SUPPORTED).
   *
   * @param itemId item primary identifier
   * @param from first base date to consult, as YYYYMM (e.g. {@code 202604}); null to default to 3
   *     base dates before {@code to}
   * @param to last base date to consult, as YYYYMM (e.g. {@code 202607}); null to default to 2
   *     months before the current one
   */
  @GET("/items/{id}/scr")
  Call<ScrResponse> getItemScr(@Path("id") String itemId, @Query("from") String from,
      @Query("to") String to);

  @GET("/accounts")
  Call<AccountsResponse> getAccounts(@Query("itemId") String itemId);

  @GET("/accounts")
  Call<AccountsResponse> getAccounts(@QueryMap AccountsRequest accountsRequest);

  @GET("/accounts/{id}")
  Call<Account> getAccount(@Path("id") String accountId);

  /**
   * Real-time balance fetched from the institution. Only available for Open Finance connectors;
   * accounts on other connectors get an error response.
   */
  @GET("/accounts/{id}/balance")
  Call<AccountBalance> getAccountBalance(@Path("id") String accountId);

  @GET("/bills")
  Call<BillsResponse> getBills(@Query("accountId") String accountId);
  
  @GET("/bills/{id}")
  Call<Bill> getBill(@Path("id") String billId);
  
  /**
   * @deprecated {@code GET /transactions} is deprecated and returns 410 for newer applications.
   * Use {@link #getTransactionsV2(String)} (cursor pagination) instead.
   */
  @Deprecated
  @GET("/transactions")
  Call<TransactionsResponse> getTransactions(@Query("accountId") String accountId);

  /**
   * @deprecated {@code GET /transactions} is deprecated and returns 410 for newer applications.
   * Use {@link #getTransactionsV2(String, TransactionsCursorSearchRequest)} (cursor pagination)
   * instead.
   */
  @Deprecated
  @GET("/transactions")
  Call<TransactionsResponse> getTransactions(@Query("accountId") String accountId,
      @QueryMap TransactionsSearchRequest transactionsSearchRequest);

  /**
   * First page of an account's transactions, cursor-paginated. Fetch the following pages by
   * passing {@link TransactionsCursorResponse#getNextCursor()} as
   * {@link TransactionsCursorSearchRequest#after(String)} until it returns null.
   */
  @GET("/v2/transactions")
  Call<TransactionsCursorResponse> getTransactionsV2(@Query("accountId") String accountId);

  @GET("/v2/transactions")
  Call<TransactionsCursorResponse> getTransactionsV2(@Query("accountId") String accountId,
      @QueryMap TransactionsCursorSearchRequest transactionsCursorSearchRequest);

  @GET("/transactions/{id}")
  Call<Transaction> getTransaction(@Path("id") String transactionId);

  @GET("/investments")
  Call<InvestmentsResponse> getInvestments(@Query("itemId") String itemId);

  @GET("/investments/{id}")
  Call<Investment> getInvestment(@Path("id") String investmentId);

  @GET("/investments/{id}/transactions")
  Call<InvestmentTransactionsResponse> getInvestmentTransactions(@Path("id") String investmentId,
      @QueryMap InvestmentTransactionsSearchRequest investmentTransactionsSearchRequest);

  @GET("/categories")
  Call<CategoriesResponse> getCategories();

  @GET("/categories/{id}")
  Call<Category> getCategory(@Path("id") String categoryId);

  @POST("/connecttokens")
  Call<ConnectTokenResponse> createConnectToken(@Body CreateConnectTokenRequest createConnectTokenRequest);
}
