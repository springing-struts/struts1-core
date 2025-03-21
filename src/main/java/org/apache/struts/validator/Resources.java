package org.apache.struts.validator;

import org.apache.commons.validator.Arg;
import org.apache.commons.validator.Field;
import org.apache.commons.validator.ValidatorAction;
import org.apache.struts.util.MessageResources;

import java.util.Locale;

/**
 * This class helps provides some useful methods for retrieving objects from
 * different scopes of the application.
 */
public class Resources {
  private Resources() {}

  /**
   * Gets the locale sensitive message based on the `ValidatorAction` message
   * and the Field's arg objects.
   */
  public static String getMessage(
    MessageResources messages,
    Locale locale,
    ValidatorAction validatorAction,
    Field field
  ) {
    var message = field.getActionMessageFor(validatorAction);
    return message.getText(messages);
  }

  /**
   * Gets the message arguments based on the given validator name and `Field`.
   */
  public static String[] getArgs(
    String validatorName,
    MessageResources messages,
    Locale locale,
    Field field
  ) {
    return field
      .getArgs(validatorName)
      .stream()
      .map(Arg::getText)
      .toArray(String[]::new);
  }
}
