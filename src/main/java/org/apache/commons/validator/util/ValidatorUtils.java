package org.apache.commons.validator.util;

import static springing.util.ObjectUtils.isEmpty;
import static springing.util.ObjectUtils.retrieveValue;

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
    @Nullable String replaceValue
  ) {
    if (value == null || key == null || replaceValue == null) {
      return value;
    }
    return value.replace(key, replaceValue);
  }

  /**
   * Convenience method for getting a value from a bean property as a String.
   * If the property is a String[] or Collection, and it is empty, an empty
   * String "" is returned. Otherwise, property.toString() is returned. This
   * method may return null if there was an error retrieving the property.
   */
  public static String getValueAsString(Object bean, String property) {
    var value = retrieveValue(bean, property);
    return isEmpty(value) ? "" : value.toString();
  }
}
