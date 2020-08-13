# Pluggy Java

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
  <version>0.3.1</version>
</dependency>
```

### Basic Usage

```java

// Use builder to create a client instance
PluggyClient pluggyClient = PluggyClient.builder()
  .clientIdAndSecret("your_client_id", "your_secret")
  .baseUrl("https://api.pluggy.ai")
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
