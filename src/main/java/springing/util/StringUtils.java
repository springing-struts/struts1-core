package springing.util;

import org.springframework.lang.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class StringUtils {

  public static String defaultIfEmpty(@Nullable String value, String defaultValue) {
    return (value == null || value.isEmpty()) ? defaultValue : value;
  }

  /**
   * Converts tokens into a lower camel case formatted string.
   */
  public static String lowerCamelize(String... texts) {
    var prefix = "";
    var buff = new StringBuilder();
    for (var text : texts) {
      if (text.isBlank()) {
        continue;
      }
      if (buff.isEmpty()) {
        var m = HAS_PRIVATE_MEMBER_PREFIX.matcher(text);
        if (m.matches()) {
          prefix = m.group(1);
        }
      }
      for (var token : tokenizeIdentifier(text)) {
        if (token.isEmpty()) {
          continue;
        }
        var initialChar = token.substring(0, 1);
        buff.append(
          buff.isEmpty() ? initialChar.toLowerCase() : initialChar.toUpperCase()
        );
        if (token.length() > 1) {
          buff.append(token.substring(1).toLowerCase());
        }
      }
    }
    return prefix + buff;
  }
  private static final Pattern HAS_PRIVATE_MEMBER_PREFIX = Pattern.compile("^\\s*(_+).*$");

  public static List<String> tokenizeIdentifier(@Nullable String token) {
    if (token == null || token.isBlank()) {
      return List.of();
    }
    return Arrays.stream(TOKEN_SEPARATOR.split(token)).toList();
  }
  private static final Pattern TOKEN_SEPARATOR = Pattern.compile(
    "([-_\\s]+|(?<=[-_a-z0-9])(?=[A-Z]))"
  );

  public static String normalizeForwardPath(@Nullable String path) {
    return ("/" + (path == null ? "" : path))
      .replaceAll("/+", "/");
//      .replaceAll("/$", "");
//      .replaceAll("\\?.+", "")
//      .replaceAll("(.)/$", "$1");
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
