# pluggy-java

Java Bindings for the Pluggy API (https://docs.pluggy.ai/).

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
ConnectorsResponse connectorsResponse = pluggyClient.getConnectors();

// or, with filters:
ConnectorsResponse connectorsResponseFiltered = pluggyClient.getConnectors(new ConnectorSearchRequest("Pluggy", Arrays.asList("AR")));

// Unsuccessful responses may be handled as well:
try {
  ConnectorsResponse connectorsResponse = pluggyClient.getConnectors();
} catch (PluggyException e) {
  System.out.println("Pluggy API error response, status " + e.getStatus() + ", message: " + e.getMmessage() + ", API message: " + e.getApiMessage()
 );
}
```
