package org.apache.struts.taglib.html;

public class Constants {
  private Constants() {}

  public static boolean isReservedFormPropertyName(String propertyName) {
    return propertyName.startsWith(TAGLIB_PACKAGE + ".");
  }

  /**
   * The name of this package.
   */
  public static final String TAGLIB_PACKAGE = "org.apache.struts.taglib.html";

  /**
   * The attribute key for the bean our form is related to.
   */
  public static final String BEAN_KEY = TAGLIB_PACKAGE + ".BEAN";

  /**
   * The property under which a Cancel button press is reported.
   */
  public static final String CANCEL_PROPERTY = TAGLIB_PACKAGE + ".CANCEL";
}
