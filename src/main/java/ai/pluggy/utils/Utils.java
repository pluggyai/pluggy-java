package ai.pluggy.utils;

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
}
