package springing.util;

import org.springframework.lang.Nullable;

import java.util.regex.Pattern;

public class StringUtils {

  public static String defaultIfEmpty(@Nullable String value, String defaultValue) {
    return (value == null || value.isEmpty()) ? defaultValue : value;
  }

  public static String normalizeForwardPath(@Nullable String path) {
    return ("/" + (path == null ? "" : path))
      .replaceAll("/+", "/")
//      .replaceAll("\\?.+", "")
      .replaceAll("(.)/$", "$1");
  }

  public static String getExtensionOf(@Nullable String path) {
    if (path == null || path.isEmpty()) {
      return "";
    }
    var m = EXTENSION_IN_PATH.matcher(path);
    if (!m.matches()) {
      return "";
    }
    return m.group(1);
  }
  private static final Pattern EXTENSION_IN_PATH = Pattern.compile(
      "^.*\\.([0-9a-zA-Z]{1,9})(\\?[^?]+)?$"
  );
}
