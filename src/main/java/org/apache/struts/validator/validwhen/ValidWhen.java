package org.apache.struts.validator.validwhen;

import org.apache.commons.validator.Field;
import org.apache.commons.validator.Validator;
import org.apache.commons.validator.ValidatorAction;
import org.apache.struts.action.ActionMessages;

import javax.servlet.http.HttpServletRequest;

/**
 * This class contains the `validwhen` validation that is used in the
 * `validator-rules.xml` file.
 */
public class ValidWhen {
  private ValidWhen() {}

  /**
   * Checks if the field matches the boolean expression specified in `test`
   * parameter.
   */
  public static boolean validateValidWhen(
    Object bean,
    ValidatorAction validatorAction,
    Field field,
    ActionMessages messages,
    Validator validator,
    HttpServletRequest request
  ) {
    return false;
  }
}
