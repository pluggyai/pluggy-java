package ai.pluggy.utils;

public abstract class Assertions {

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
}
