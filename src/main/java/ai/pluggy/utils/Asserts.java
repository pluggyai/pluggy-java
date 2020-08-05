package ai.pluggy.utils;

import okhttp3.HttpUrl;

public abstract class Asserts {

  /**
   * Asserts that an object is not null.
   *
   * @param value the value to check.
   * @param name  the name of the parameter, used when creating the exception message.
   * @throws IllegalArgumentException if the value is null
   */
  public static void assertNotNull(Object value, String name) {
    if (value == null) {
      throw new IllegalArgumentException(String.format("'%s' cannot be null", name));
    }
  }

  /**
   * Asserts that a value is a valid URL.
   *
   * @param value the value to check.
   * @param name the name of the parameter, used when creating the exception message.
   * @throws IllegalArgumentException if the value is null or is not a valid URL.
   */
  public static void assertValidUrl(String value, String name) {
    if (value == null || HttpUrl.parse(value) == null) {
      throw new IllegalArgumentException(String.format("'%s' must be a valid URL!", name));
    }
  }

}
