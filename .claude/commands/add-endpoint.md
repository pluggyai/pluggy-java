# Add New Endpoint

Add a new API endpoint to the Pluggy Java SDK.

## Required Information

- **Endpoint path**: e.g. `/models`, `/models/{id}`
- **HTTP method**: `GET`, `POST`, `PATCH`, `DELETE`
- **Query / path / body parameters**: from the OAS
- **Response schema**: from the OAS
- **Request schema** (if any): from the OAS

## Steps

### 1. Define request and response DTOs

Create DTOs under:
- `src/main/java/ai/pluggy/client/response/` — response payloads (`@Data @Builder`)
- `src/main/java/ai/pluggy/client/request/` — request bodies (`@Value @AllArgsConstructor @Builder`) or query maps

See `add-model-fields` for field patterns. **Field names must match the OAS byte-for-byte** (Gson `IDENTITY`).

Naming conventions:
- Single item response: bare entity name — `Account`, `Bill`, `Transaction`
- Collection / paginated response: `<Entity>sResponse` — `AccountsResponse`, `BillsResponse`
- Request body: `<Verb><Entity>Request` — `CreateItemRequest`, `UpdateItemRequest`
- Query map: `<Entity>SearchRequest` or `<Entity>Request` — `TransactionsSearchRequest`, `AccountsRequest`

### 2. Add the method to `PluggyApiService`

`src/main/java/ai/pluggy/client/PluggyApiService.java` is the single Retrofit interface for the entire SDK. Add the new method there with the appropriate annotations.

**Retrofit patterns:**

```java
// GET with no params
@GET("/categories")
Call<CategoriesResponse> getCategories();

// GET with path param
@GET("/categories/{id}")
Call<Category> getCategory(@Path("id") String categoryId);

// GET with single query param
@GET("/accounts")
Call<AccountsResponse> getAccounts(@Query("itemId") String itemId);

// GET with query map (filters / pagination)
@GET("/accounts")
Call<AccountsResponse> getAccounts(@QueryMap AccountsRequest accountsRequest);

// POST with body
@POST("/items")
Call<ItemResponse> createItem(@Body CreateItemRequest createItemRequest);

// PATCH with path param + body
@PATCH("/items/{id}")
Call<ItemResponse> updateItem(@Path("id") String itemId,
    @Body UpdateItemRequest updateItemRequest);

// DELETE with path param
@DELETE("/items/{id}")
Call<DeleteItemResponse> deleteItem(@Path("id") String existingItemId);
```

Provide overloads (e.g. a no-body `updateItem` and a body-accepting one) only if the API genuinely supports both shapes — don't add convenience overloads speculatively.

### 3. Build and verify compilation

```bash
mvn -B package
```

### 4. Add an integration test

Put the test under `src/test/java/ai/pluggy/client/integration/` extending `BaseApiIntegrationTest`. The base class provides an authenticated `client` and cleans up items registered in `itemsIdCreated` after each test.

```java
class GetModelTest extends BaseApiIntegrationTest {

  @Test
  void getModel_validId_responseOk() throws IOException {
    Response<Model> response = client.service().getModel("some-id").execute();
    assertSuccessful(response);  // from integration/util/AssertionsUtils
    Model model = response.body();
    assertNotNull(model);
    assertEquals("some-id", model.getId());
  }
}
```

If the test creates items, **register them** in `itemsIdCreated` immediately after creation so the `@AfterEach` cleanup deletes them — otherwise they leak into production.

### 5. Run the integration test

```bash
doppler run -- mvn -B verify -Dit.test=GetModelTest -DfailIfNoTests=false
```

### 6. Update CLAUDE.md (optional)

If the new endpoint is a notable addition (e.g. a whole new resource family), mention it under "Architecture".
