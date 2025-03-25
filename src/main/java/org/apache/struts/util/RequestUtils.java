package org.apache.struts.util;

import static springing.util.ObjectUtils.classFor;
import static springing.util.ObjectUtils.createInstanceOf;

import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import org.springframework.context.i18n.LocaleContextHolder;

/**
 * General purpose utility methods related to processing a servlet request in
 * the Struts controller framework.
 */
public class RequestUtils {

  private RequestUtils() {}

  /**
   * Look up and return current user locale, based on the specified parameters.
   */
  public static Locale getUserLocale(
    HttpServletRequest request,
    String locale
  ) {
    return LocaleContextHolder.getLocale();
  }

  /**
   * Return a new instance of the specified fully qualified class name, after
   * loading the class from this web application's class loader. The specified
   * class **MUST** have a public zero-arguments constructor.
   */
  public static Object applicationInstance(String className) {
    return createInstanceOf(className);
  }

  /**
   * Return the `Class` object for the specified fully qualified class name,
   * from this web application's class loader.
   */
  public static Class<?> applicationClass(String className) {
    return classFor(className);
  }
}
