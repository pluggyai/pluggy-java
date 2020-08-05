package ai.pluggy.client.integration;

import ai.pluggy.client.PluggyClient;
import org.junit.jupiter.api.BeforeEach;

class BaseApiIntegrationTest {

  static final String TEST_BASE_URL = System.getenv("PLUGGY_BASE_URL");
  static final String CLIENT_ID = "906a15c0-fdde-4dc5-9a23-df44455e1fb4";
  static final String CLIENT_SECRET = "6fc93aec-9166-417c-8363-669167a39ce4";
  PluggyClient client;

  @BeforeEach
  void setUp() {
    client = PluggyClient.builder()
      .baseUrl(TEST_BASE_URL)
      .clientIdAndSecret(CLIENT_ID, CLIENT_SECRET)
      .build();
  }
}
