package org.apache.struts.util;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.lang.Nullable;
import springing.struts1.message.ResourceBundleMessageResources;

import java.util.Locale;

import static java.lang.String.format;
import static java.util.Objects.requireNonNullElseGet;

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
public abstract class MessageResources {

  /**
   * Create and return an instance of `MessageResources` for the created by
   * the default `MessageResourcesFactory`
   */
  public static MessageResources getMessageResources(String config) {
    return ResourceBundleMessageResources.load(config);
  }

  /**
   * Returns a text message for the specified key, for the default Locale.
   */
  public @Nullable String getMessage(String key, Object... args) {
    return getMessage(null, key, args);
  }

  /**
   * Returns a text message for the specified key, for the default Locale.
   * Throws an exception if there is no message associated with the given key.
   */
  public String requireMessage(String key, Object... args) {
    return requireNonNullElseGet(getMessage(null, key, args), () -> {
      throw new IllegalArgumentException(format(
        "The message associated with the key [%s] is not defined in the message resource [%s].",
        key, getConfig()
      ));
    });
  }

  /**
   * Returns a text message for the specified key, for the default Locale.
   * A null string result will be returned by this method if no relevant
   * message resource is found for this key or Locale, if the`returnNull`
   * property is set.  Otherwise, an appropriate error message will be
   * returned. This method must be implemented by a concrete subclass.
   */
  public abstract @Nullable String getMessage(
    @Nullable Locale locale, String key, Object... args
  );

  public @Nullable String getMessageInLocale(
    @Nullable String locale, String key, Object... args
  ) {
    Locale localeObj = locale != null ? Locale.forLanguageTag(locale) : LocaleContextHolder.getLocale();
    return getMessage(localeObj, key, args);
  }

  /**
   * The configuration parameter used to initialize this MessageResources.
   */
  public abstract String getConfig();

  /**
   * Returns `true` if there is a defined message for the specified key in the
   * specified Locale.
   */
  public boolean isPresent(@Nullable Locale locale, String key) {
    var message = getMessage(locale, key);
    return message != null;
  }

  /**
   * Returns `true` if there is a defined message for the specified key in the
   * default Locale.
   */
  public boolean isPresent(String key) {
    var message = getMessage(key);
    return message != null;
  }
}
