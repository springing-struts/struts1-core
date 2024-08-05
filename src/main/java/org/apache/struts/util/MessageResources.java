package org.apache.struts.util;

import springing.struts1.configuration.MessageResourcesConfiguration;

import java.util.Locale;

/**
 * General purpose abstract class that describes an API for retrieving
 * Locale-sensitive messages from underlying resource locations of an
 * unspecified design, and optionally utilizing the `MessageFormat` class to
 * produce internationalized messages with parametric replacement. Calls to
 * `getMessage()` variants without a `Locale` argument are presumed to be
 * requesting a message string in the default `Locale` for this JVM.
 * Calls to `getMessage()` with an unknown key, or an unknown `Locale` will
 * return `null` if the `returnNull` property is set to `true`. Otherwise, a
 * suitable error message will be returned instead.
 * **IMPLEMENTATION NOTE**
 * Classes that extend this class must be Serializable so that instances may be
 * used in distributable application server environments.
 */
public interface MessageResources {
  /**
   * Create and return an instance of `MessageResources` for the created by
   * the default `MessageResourcesFactory`
   */
  static MessageResources getMessageResources(String config) {
    return MessageResourcesConfiguration.getMessageResources(config);
  }

  /**
   * Returns a text message for the specified key, for the default Locale.
   */
  String getMessage(String key, Object... args);

  /**
   * Returns a text message for the specified key, for the default Locale.
   * A null string result will be returned by this method if no relevant
   * message resource is found for this key or Locale, if the`returnNull`
   * property is set.  Otherwise, an appropriate error message will be
   * returned. This method must be implemented by a concrete subclass.
   */
  String getMessage(Locale locale, String key, Object... args);

  /**
   * The configuration parameter used to initialize this MessageResources.
   */
  String getConfig();
}
