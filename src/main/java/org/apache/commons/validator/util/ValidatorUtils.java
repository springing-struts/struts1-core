package org.apache.commons.validator.util;

import org.springframework.lang.Nullable;

/**
 * Basic utility methods.
 * **NOTE**
 *   The use of FastHashMap is deprecated and will be replaced in a future
 *   release.
 */
public class ValidatorUtils {
  private ValidatorUtils() {}

  /**
   * Replace part of a String with another value.
   */
  public static @Nullable String replace(
    @Nullable String value,
    @Nullable String key,
    @Nullable String replaceValue) {
    if (value == null || key == null || replaceValue == null) {
      return value;
    }
    return value.replace(key, replaceValue);
  }
}
