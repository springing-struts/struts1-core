package org.apache.struts.util;

import springing.util.ObjectUtils;

import static springing.util.ObjectUtils.classFor;
import static springing.util.ObjectUtils.createInstanceOf;

/**
 * General purpose utility methods related to processing a servlet request in
 * the Struts controller framework.
 */
public class RequestUtils {
  private RequestUtils() {}

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
