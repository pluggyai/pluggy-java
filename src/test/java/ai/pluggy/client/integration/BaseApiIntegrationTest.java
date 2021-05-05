package ai.pluggy.client.integration;

import ai.pluggy.client.PluggyClient;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.platform.commons.util.StringUtils;

class BaseApiIntegrationTest {

  static final String CLIENT_ID = System.getenv("PLUGGY_CLIENT_ID");
  static final String CLIENT_SECRET = System.getenv("PLUGGY_CLIENT_SECRET");
  static final String TEST_BASE_URL = System.getenv("PLUGGY_BASE_URL");

  PluggyClient client;

  @BeforeEach
  void setUp() {
    checkEnvErrors();
    client = PluggyClient.builder()
      .baseUrl(TEST_BASE_URL)
      .clientIdAndSecret(CLIENT_ID, CLIENT_SECRET)
      .build();
  }

  protected void checkEnvErrors() {
    List<String> missingEnvVars = new ArrayList<>();
    if (StringUtils.isBlank(CLIENT_ID)) {
      missingEnvVars.add("PLUGGY_CLIENT_ID");
    }
    if (StringUtils.isBlank(CLIENT_SECRET)) {
      missingEnvVars.add("PLUGGY_CLIENT_SECRET");
    }
    if (StringUtils.isBlank(TEST_BASE_URL)) {
      missingEnvVars.add("PLUGGY_BASE_URL");
    }
    if (missingEnvVars.size() > 0) {
      String envVarsListString = missingEnvVars.stream()
        .map(varName -> "'" + varName + "'")
        .collect(Collectors.joining(", "));
      throw new IllegalStateException("Must define " + envVarsListString + " env var(s)!");
    }
  }
  
}
