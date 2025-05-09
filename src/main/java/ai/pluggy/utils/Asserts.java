package ai.pluggy.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

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
   * Asserts that an array is not empty.
   *
   * @param value the value to check.
   * @param name  the name of the parameter, used when creating the exception message.
   * @throws IllegalArgumentException if the value is null
   */
  public static void assertNotEmpty(String[] value, String name) {
    if (value != null && value.length < 1) {
      throw new IllegalArgumentException(String.format("'%s' cannot be empty", name));
    }
  }

  /**
   * Asserts that a value is a valid URL.
   *
   * @param value the value to check.
   * @param name  the name of the parameter, used when creating the exception message.
   * @throws IllegalArgumentException if the value is null or is not a valid URL.
   */
  public static void assertValidUrl(String value, String name) {
    if (value == null || HttpUrl.parse(value) == null) {
      throw new IllegalArgumentException(String.format("'%s' must be a valid URL!", name));
    }
  }


  /**
   * Asserts that a string value is a valid Date string, according to a specified dateFormatPattern.
   * The pattern must be supported by DateTimeFormatter (See: <a href="https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html"/>)
   *
   * @param value             the value to check.
   * @param dateFormatPattern the date string pattern to use for validating the date.
   * @param name              the name of the parameter, used when creating the exception message.
   * @throws IllegalArgumentException if the value is null or is not a valid URL.
   */
  public static void assertValidDateString(String value, String dateFormatPattern,
    String name) {
    DateTimeFormatter dateFormat;
    try {
      dateFormat = DateTimeFormatter.ofPattern(dateFormatPattern);
    } catch (IllegalArgumentException e) {
      String invalidDatePatternErrorMsg = String.format(
        "Invalid dateFormatPattern string: '%s'", dateFormatPattern);
      throw new IllegalArgumentException(invalidDatePatternErrorMsg);
    }

    try {
      LocalDate.parse(value, dateFormat);
    } catch (DateTimeParseException e) {
      String invalidValueErrorMsg = String.format(
        "Invalid date string format '%s' for field '%s', expected format: '%s'!",
        value, name, dateFormatPattern);
      throw new IllegalArgumentException(invalidValueErrorMsg);
    }
  }
}
