package springing.util;

import jakarta.annotation.Nullable;

public class StringUtils {
  public static String defaultIfEmpty(@Nullable String value, String defaultValue) {
    return (value == null || value.isEmpty()) ? defaultValue : value;
  }
}
