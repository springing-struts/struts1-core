package org.apache.struts.util;

import springing.util.ObjectUtils;

/**
 * General purpose utility methods related to processing a servlet request in
 * the Struts controller framework.
 */
public class RequestUtils {
  private RequestUtils() {}

  /**
   * Return the `Class` object for the specified fully qualified class name,
   * from this web application's class loader.
   */
  public static Class<?> applicationClass(String className) {
    return ObjectUtils.classFor(className);
  }
}
