package org.apache.struts.util;

import org.springframework.lang.Nullable;

import static org.springframework.util.StringUtils.hasText;
import static org.springframework.web.util.HtmlUtils.htmlEscape;

/**
 * General purpose utility methods related to generating a servlet response in the Struts controller framework.
 */
public class ResponseUtils {
  private ResponseUtils() {
  }

  /**
   * Filter the specified string for characters that are sensitive to HTML
   * interpreters, returning the string with these characters replaced by the
   * corresponding character entities.
   */
  public static @Nullable String filter(@Nullable String text) {
    return hasText(text) ? htmlEscape(text) : text;
  }
}
