package org.apache.struts.taglib;

import org.apache.struts.Globals;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.lang.Nullable;

import javax.servlet.jsp.PageContext;
import java.util.Locale;

import static java.util.Objects.requireNonNull;

public class TagUtils {

  public static TagUtils getInstance() {
    return requireNonNull(
      Holder.INSTANCE,
      "The TagUtils instance for the current request is not initialized yet."
    );
  }

  public static class Holder {
    public Holder(TagUtils instance) {
      INSTANCE = instance;
    }
    private static @Nullable TagUtils INSTANCE;
  }

  /**
   * Look up and return current user locale, based on the specified parameters.
   * @param locale Name of the session attribute for our user's Locale. If this
   *               is `null`, the default locale key is used for the lookup.
   */
  public Locale getUserLocale(PageContext pageContext, @Nullable String locale) {
    return LocaleContextHolder.getLocale();
  }

  /**
   * Returns true if the custom tags are in XHTML mode.
   */
  public boolean isXhtml(PageContext pageContext) {
    return "true".equals(pageContext.findAttribute(Globals.XHTML_KEY));
  }
}
