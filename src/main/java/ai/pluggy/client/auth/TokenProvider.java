package ai.pluggy.client.auth;

/**
 * Helper for apiKey storage between httpClient calls.
 */
public class TokenProvider {

  private String apiKey;

  public String getApiKey() {
    return apiKey;
  }

  public void setApiKey(String apiKey) {
    this.apiKey = apiKey;
  }
}
