package org.apache.struts.util;

import org.springframework.context.support.ResourceBundleMessageSource;
import springing.struts1.configuration.MessageResourcesConfiguration;

import java.util.Locale;

public interface MessageResources {
  /**
   * Create and return an instance of `MessageResources` for the created by
   * the default `MessageResourcesFactory`
   */
  public static MessageResources getMessageResources(String config) {
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
