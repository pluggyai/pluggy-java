package ai.pluggy.utils;

import ai.pluggy.client.response.WebhookEventPayload;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.FileReader;
import java.io.IOException;

public abstract class Utils {

  public static String getSdkVersion() {
    try {
      MavenXpp3Reader reader = new MavenXpp3Reader();
      Model model = reader.read(new FileReader("pom.xml"));
      return model.getVersion();
    } catch(IOException | XmlPullParserException e) {
      return "";
    }
  }

  public static <T> T parseJsonResponse(String responseBodyString, Class<T> clazz)
    throws JsonSyntaxException {
    if (responseBodyString == null) {
      throw new JsonSyntaxException("Response body is null");
    }
    if (responseBodyString.length() == 0) {
      throw new JsonSyntaxException("Response body is empty");
    }

    return new Gson().fromJson(responseBodyString, clazz);
  }

  /**
   * Parse the Json received from the webhook event
   * @param responseBodyString
   * @return the Json parsed as WebhookEventPayload
   */
  public static WebhookEventPayload parseWebhookEventPayload(String responseBodyString) {
    return parseJsonResponse(responseBodyString, WebhookEventPayload.class);
  }

  /**
   * Get the ConnectorId enum constant from an integer value
   * @param value the integer value
   * @return the corresponding ConnectorId enum constant
   * @throws IllegalArgumentException if the value does not correspond to any ConnectorId
   */
  public static ConnectorId getConnectorIdFromValue(int value) {
    return ConnectorId.fromValue(value);
  }

  /**
   * Get the integer value from a ConnectorId enum constant
   * @param connectorId the ConnectorId enum constant
   * @return the corresponding integer value
   */
  public static int getValueFromConnectorId(ConnectorId connectorId) {
    return connectorId.getValue();
  }
}
