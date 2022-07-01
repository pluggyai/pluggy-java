package ai.pluggy.utils;

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
    }
    catch(IOException | XmlPullParserException e) {
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
}
