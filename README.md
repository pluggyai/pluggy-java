# pluggy-java

Java Bindings for the Pluggy API (https://www.plaid.com/docs).

For available SDK API methods, check [PluggyApiService](./src/main/java/ai/pluggy/client/pluggy/client/PluggyApiService.java) interface methods.

Check the Junit test classes for examples of more use cases. Every API endpoint has at
least one integration test against a Pluggy Platform API instance environment.

Uses [Retrofit](https://github.com/square/retrofit) and [OkHttp](https://github.com/square/okhttp) under
the hood. You may want to take a look at those libraries if you need to do anything out of the ordinary.

### Install

Using Maven, add dependency to your pom.xml:

```xml
<dependency>
  <groupId>ai.pluggy</groupId>
  <artifactId>pluggy-java</artifactId>
  <version>0.1.0-SNAPSHOT</version>
</dependency>
```

### Basic Usage

```java

// Use builder to create a client instance
PluggyClient pluggyClient = PluggyClient.builder()
  .clientIdAndSecret("your_client_id", "your_secret")
  .build();

// Authenticate your client (optional - will be managed automatically on any unauthenticated request attempt) 
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
