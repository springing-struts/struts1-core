package org.apache.struts.util;

import org.apache.struts.action.ActionMessage;
import org.springframework.lang.Nullable;

/**
 * Used for specialized exception handling.
 */
public class ModuleException extends Exception {

  private final ActionMessage message;
  private @Nullable String property;

  /**
   * Constructs a module exception using the specified message resource key.
   */
  public ModuleException(String key) {
    super(key);
    message = new ActionMessage(key);
  }

  /**
   * Constructs a module exception using the specified message resource key and
   * replacement values.
   */
  public ModuleException(String key, Object... values) {
    super(key);
    message = new ActionMessage(key, values);
  }

  /**
   * Set the property associated with the exception. It can be a name of the
   * edit field, which 'caused' the exception.
   */
  public void setProperty(String property) {
    this.property = property;
  }

  /**
   * Returns the property associated with the exception.
   */
  public String getProperty() {
    return property == null ? message.getKey() : property;
  }
}
