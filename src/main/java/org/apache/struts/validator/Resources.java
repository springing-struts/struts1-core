package org.apache.struts.validator;

import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.validator.Arg;
import org.apache.commons.validator.Field;
import org.apache.commons.validator.ValidatorAction;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.util.MessageResources;

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

  /**
   * Gets the `ActionMessage` based on the `ValidatorAction` message and the
   * Field's arg objects.
   * **Note**:
   * This method does not respect bundle information stored with the field's
   * `msg` or `arg` elements, and localization will not work for alternative
   * resource bundles. This method is deprecated for this reason, and you
   * should use `getActionMessage(Validator,HttpServletRequest,ValidatorAction,Field)`
   * instead.
   */
  public static ActionMessage getActionMessage(
    HttpServletRequest request,
    ValidatorAction validatorAction,
    Field field
  ) {
    return field.getActionMessageFor(validatorAction);
  }
}
