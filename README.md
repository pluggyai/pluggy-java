# Pluggy Java

[![Actions Status](https://github.com/pluggyai/pluggy-java/workflows/Build%20%26%20Test/badge.svg)](https://github.com/pluggyai/pluggy-java/actions?query=workflow%3A"Build+%26+Test")

Java Bindings for the Pluggy API (https://docs.pluggy.ai/).

For available SDK API methods, check [PluggyApiService](./src/main/java/ai/pluggy/client/PluggyApiService.java) interface methods.

This implementation uses [Retrofit](https://github.com/square/retrofit) and [OkHttp](https://github.com/square/okhttp) libraries. For advanced use cases, please check their respectives APIs.

Also, for examples of use, please check the [integration tests](./src/test/java/ai/pluggy/client/integration) - practically all of the available endpoints have at least one test case.

### Install

Using Maven, add dependency to your pom.xml:

Currently, the package is available in Github Packages, so make sure to have the GH Packages server config with your Personal GH Access Token in your `.m2/settings.xml` file. Navigate to [this guide](https://docs.github.com/en/packages/using-github-packages-with-your-projects-ecosystem/configuring-apache-maven-for-use-with-github-packages#authenticating-to-github-packages) for more details.

```xml
<dependency>
  <groupId>ai.pluggy</groupId>
  <artifactId>pluggy-java</artifactId>
  <version>1.7.0</version>
</dependency>
```

### Basic Usage

```java

// Use builder to create a client instance
PluggyClient pluggyClient = PluggyClient.builder()
  .clientIdAndSecret("your_client_id", "your_secret")
  .build();

// Authenticate your client (optional - by default, auth token is requested & refreshed as needed by ApiKeyAuthInterceptor)
pluggyClient.authenticate()

// Synchronously perform a request
Response<ConnectorsResponse> connectorsResponse = pluggyClient.service().getConnectors().execute();

// or, a request with params:
Response<ConnectorsResponse> connectorsResponseFiltered = pluggyClient.service()
  .getConnectors(new ConnectorSearchRequest("Pluggy", Arrays.asList("AR")))
  .execute();

// Read response data (or error):
if(connectorsResponse.isSuccessful()) {
  // successful -> get body
  ConnectorsResponse connectorsResponseBody = connectorsResponse.body();
} else {
  // unsuccessful -> parse error data
  ErrorResponse errorResponse = pluggyClient.parseError(connectorsResponse)
}
```

### Transactions (cursor pagination)

`getTransactions` (`GET /transactions`) is deprecated and returns `410` for newer applications. Use `getTransactionsV2` (`GET /v2/transactions`) and follow the cursor until there is no next page:

```java
List<Transaction> transactions = new ArrayList<>();
TransactionsCursorSearchRequest request = new TransactionsCursorSearchRequest().dateFrom("2026-01-01");
while (true) {
  TransactionsCursorResponse page = pluggyClient.service()
    .getTransactionsV2(accountId, request)
    .execute()
    .body();
  transactions.addAll(page.getResults());
  if (!page.hasNext()) {
    break;
  }
  request = new TransactionsCursorSearchRequest().dateFrom("2026-01-01").after(page.getNextCursor());
}
```

### Listing items (opt-in)

> **Opt-in, paid plans only.** Listing items is disabled by default and is only available to paid-plan teams that have explicitly requested it from Pluggy support. Teams without it enabled get `403` with `codeDescription` `LIST_ITEMS_FEATURE_NOT_ENABLED`. For most integrations, store each `itemId` when it is created (Pluggy Connect `onSuccess` or the `item/created` webhook) and use `getItem(id)` instead.

`getItems` (`GET /v2/items`) returns items newest first, optionally filtered by `clientUserId` and/or `connectorId`, and is cursor-paginated like `getTransactionsV2`:

```java
List<ItemResponse> items = new ArrayList<>();
ItemsCursorSearchRequest request = new ItemsCursorSearchRequest().clientUserId("user-123");
while (true) {
  Response<ItemsCursorResponse> response = pluggyClient.service().getItems(request).execute();
  if (!response.isSuccessful()) {
    ErrorResponse error = pluggyClient.parseError(response);
    // "LIST_ITEMS_FEATURE_NOT_ENABLED" when listing items is not enabled for your team
    throw new IllegalStateException(error.getCodeDescription() + ": " + error.getMessage());
  }
  ItemsCursorResponse page = response.body();
  items.addAll(page.getResults());
  if (!page.hasNext()) {
    break;
  }
  request = new ItemsCursorSearchRequest().clientUserId("user-123").after(page.getNextCursor());
}
```

### Smart Transfers

A Smart Transfer preauthorization is the payer's consent, given once at their bank, to send transfers to a set of payment recipients. Create it, send the payer to its `consentUrl`, and once its status is `COMPLETED` create payments under it:

```java
CreateSmartTransferPreauthorizationRequest request = CreateSmartTransferPreauthorizationRequest.builder()
  .connectorId(connectorId)
  .parameters(new SmartTransferPreauthorizationParameter("12345678900"))
  .recipientIds(Collections.singletonList(recipientId))
  .linkedJourney(true) // optional: also ask for permission to read the source account balance
  .build();
SmartTransferPreauthorization preauthorization = pluggyClient.service()
  .createSmartTransferPreauthorization(request)
  .execute()
  .body();
// redirect the payer to preauthorization.getConsentUrl()

SmartTransferPayment payment = pluggyClient.service()
  .createSmartTransferPayment(new CreateSmartTransferPaymentRequest(preauthorization.getId(), recipientId, 100.0))
  .execute()
  .body();
```

With `linkedJourney`, `getSmartTransferPreauthorizationBalance(id)` reads the source account balance (and its overdraft, when the institution shares it) once `dataConsent` is `AUTHORISED`. Each call reads it from the institution in real time and counts toward that account's monthly Open Finance quota. `cancelSmartTransferPreauthorizationDataConsent(id)` cancels only that permission; the preauthorization keeps working.
