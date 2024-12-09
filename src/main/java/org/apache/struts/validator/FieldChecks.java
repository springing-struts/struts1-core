package org.apache.struts.validator;

import org.apache.commons.validator.Field;
import org.apache.commons.validator.Validator;
import org.apache.commons.validator.ValidatorAction;
import org.apache.struts.action.ActionMessages;
import javax.servlet.http.HttpServletRequest;

/**
 * This class contains the default validations that are used in the
 * `validator-rules.xml` file.
 * In general passing in a null or blank will return a null Object or a false
 * boolean. However, nulls and blanks do not result in an error being added to
 * the errors.
 */
public class FieldChecks {

  private FieldChecks() {}

  /**
   * Checks if the field isn't null and length of the field is greater than
   * zero not including whitespace.
   */
  public static boolean validateRequired(
    Object bean,
    ValidatorAction action,
    Field field,
    ActionMessages errors,
    Validator validator,
    HttpServletRequest request
  ) {
    var value = field.getValueOf(bean);
    if (value == null) {
      return true;
    }
    var valid = !value.toString().isBlank();
    if (!valid) {
      errors.addValidationError(field, action);
    }
    return valid;
  }

  /**
   * Checks if the field isn't null based on the values of other fields.
   */
  public static boolean validateRequiredIf(
    Object bean,
    ValidatorAction validatorAction,
    Field field,
    ActionMessages errors,
    Validator validator,
    HttpServletRequest request
  ) {
    return false;
  }

  /**
   * Checks if the field's length is greater than or equal to the minimum
   * value. A `Null` will be considered an error.
   */
  public static boolean validateMinLength(
    Object bean,
    ValidatorAction validatorAction,
    Field field,
    ActionMessages errors,
    Validator validator,
    HttpServletRequest request
  ) {
    var minLength = field.getRequiredVarNumber("minlength");
    var value = field.getValueOf(bean);
    if (value == null) {
      return true;
    }
    var length = value.toString().length();
    var valid = length == 0 || minLength <= length;
    if (!valid) {
      errors.addValidationError(field, validatorAction);
    }
    return valid;
  }

  /**
   * Checks if the field's length is less than or equal to the maximum value.
   * A `Null` will be considered an error.
   */
  public static boolean validateMaxLength(
    Object bean,
    ValidatorAction validatorAction,
    Field field,
    ActionMessages errors,
    Validator validator,
    HttpServletRequest request
  ) {
    var maxLength = field.getRequiredVarNumber("maxlength");
    var value = field.getValueOf(bean);
    if (value == null) {
      return true;
    }
    var length = value.toString().length();
    var valid = length == 0 || length <= maxLength;
    if (!valid) {
      errors.addValidationError(field, validatorAction);
    }
    return valid;
  }

  /**
   * Checks if the field matches the regular expression in the field's mask
   * attribute.
   */
  public static boolean validateMask(
    Object bean,
    ValidatorAction validatorAction,
    Field field,
    ActionMessages errors,
    Validator validator,
    HttpServletRequest request
  ) {
    var value = field.getValueOf(bean);
    if (value == null) {
      return true;
    }
    var pattern = field.getRequiredVarValue("mask");
    var valid = value.toString().matches(pattern);
    if (!valid) {
      errors.addValidationError(field, validatorAction);
    }
    return false;
  }

  /**
   * Checks if the field can safely be converted to a byte primitive.
   */
  public static boolean validateByte(
    Object bean,
    ValidatorAction validatorAction,
    Field field,
    ActionMessages errors,
    Validator validator,
    HttpServletRequest request
  ) {
    return false;
  }

  /**
   * Checks if the field can safely be converted to a short primitive.
   */
  public static boolean validateShort(
    Object bean,
    ValidatorAction validatorAction,
    Field field,
    ActionMessages errors,
    Validator validator,
    HttpServletRequest request
  ) {
    return false;
  }

  /**
   * Checks if the field can safely be converted to an int primitive.
   */
  public static boolean validateInteger(
    Object bean,
    ValidatorAction validatorAction,
    Field field,
    ActionMessages errors,
    Validator validator,
    HttpServletRequest request
  ) {
    return false;
  }

  /**
   * Checks if the field can safely be converted to a long primitive.
   */
  public static boolean validateLong(
    Object bean,
    ValidatorAction validatorAction,
    Field field,
    ActionMessages errors,
    Validator validator,
    HttpServletRequest request
  ) {
    return false;
  }

  /**
   * Checks if the field can safely be converted to a float primitive.
   */
  public static boolean validateFloat(
    Object bean,
    ValidatorAction validatorAction,
    Field field,
    ActionMessages errors,
    Validator validator,
    HttpServletRequest request
  ) {
    return false;
  }

  /**
   * Checks if the field can safely be converted to a double primitive.
   */
  public static boolean validateDouble(
    Object bean,
    ValidatorAction validatorAction,
    Field field,
    ActionMessages errors,
    Validator validator,
    HttpServletRequest request
  ) {
    return false;
  }
  /**
   * Checks if the field can safely be converted to a byte primitive.
   */
  public static boolean validateByteLocale(
    Object bean,
    ValidatorAction validatorAction,
    Field field,
    ActionMessages errors,
    Validator validator,
    HttpServletRequest request
  ) {
    return false;
  }

  /**
   * Checks if the field can safely be converted to a short primitive.
   */
  public static boolean validateShortLocale(
    Object bean,
    ValidatorAction va,
    Field field,
    ActionMessages errors,
    Validator validator,
    HttpServletRequest request
  ) {
    return true;
  }

  /**
   * Checks if the field can safely be converted to an int primitive.
   */
  public static boolean validateIntegerLocale(
    Object bean,
    ValidatorAction validatorAction,
    Field field,
    ActionMessages errors,
    Validator validator,
    HttpServletRequest request
  ) {
    return false;
  }

  /**
   * Checks if the field can safely be converted to a long primitive.
   */
  public static boolean validateLongLocale(
    Object bean,
    ValidatorAction validatorAction,
    Field field,
    ActionMessages errors,
    Validator validator,
    HttpServletRequest request
  ) {
    return false;
  }

  /**
   * Checks if the field can safely be converted to a float primitive.
   */
  public static boolean validateFloatLocale(
    Object bean,
    ValidatorAction validatorAction,
    Field field,
    ActionMessages errors,
    Validator validator,
    HttpServletRequest request
  ) {
    return false;
  }

  /**
   * Checks if the field can safely be converted to a double primitive.
   */
  public static boolean validateDoubleLocale(
    Object bean,
    ValidatorAction validatorAction,
    Field field,
    ActionMessages errors,
    Validator validator,
    HttpServletRequest request
  ) {
    return false;
  }

  /**
   * Checks if the field is a valid date. If the field has a datePattern
   * variable, that will be used to format `java.text.SimpleDateFormat`. If the
   * field has a datePatternStrict variable, that will be used to format
   * `java.text.SimpleDateFormat` and the length will be checked so '2/12/1999'
   * will not pass validation with the format 'MM/dd/yyyy' because the month
   * isn't two digits. If no datePattern variable is specified, then the field
   * gets the DateFormat.SHORT format for the locale. The setLenient method is
   * set to `false` for all variations.
   */
  public static boolean validateDate(
    Object bean,
    ValidatorAction validatorAction,
    Field field,
    ActionMessages errors,
    Validator validator,
    HttpServletRequest request
  ) {
    return false;
  }

  /**
   * Checks if a fields value is within a range (min & max specified in
   * the vars attribute).
   */
  public static boolean validateIntRange(
    Object bean,
    ValidatorAction validatorAction,
    Field field,
    ActionMessages errors,
    Validator validator,
    HttpServletRequest request
  ) {
    return false;
  }

  /**
   *  Checks if a fields value is within a range (min & max specified in the
   *  vars attribute).
   */
  public static boolean validateLongRange(
    Object bean,
    ValidatorAction validatorAction,
    Field field,
    ActionMessages errors,
    Validator validator,
    HttpServletRequest request
  ) {
    return false;


  }

  /**
   * Checks if a fields value is within a range (min & max specified in the
   * vars attribute).
   */
  public static boolean validateFloatRange(
    Object bean,
    ValidatorAction validatorAction,
    Field field,
    ActionMessages errors,
    Validator validator,
    HttpServletRequest request
  ) {
    return false;
  }

  /**
   * Checks if a fields value is within a range (min & max specified in the
   * vars attribute).
   */
  public static boolean validateDoubleRange(
    Object bean,
    ValidatorAction validatorAction,
    Field field,
    ActionMessages errors,
    Validator validator,
    HttpServletRequest request
  ) {
    return false;
  }

  /**
   * Checks if the field is a valid credit card number.
   */
  public static boolean validateCreditCard(
    Object bean,
    ValidatorAction validatorAction,
    Field field,
    ActionMessages errors,
    Validator validator,
    HttpServletRequest request
  ) {
    return false;
  }

  /**
   * Checks if a field has a valid e-mail address.
   */
  public static boolean validateEmail(
    Object bean,
    ValidatorAction validatorAction,
    Field field,
    ActionMessages errors,
    Validator validator,
    HttpServletRequest request
  ) {
    return false;
  }

  /**
   * Checks if a field has a valid url. Four optional variables can be
   * specified to configure url validation.
   * - Variable `allow2slashes` can be set to `true` or `false` to control
   *   whether two slashes are allowed - default is `false` (i.e. two slashes
   *   are NOT allowed).<
   * - Variable `nofragments` can be set to `true` or `false` to control
   *   whether fragments are allowed - default is `false` (i.e. fragments ARE
   *   allowed).
   * - Variable `allowallschemes` can be set to `true` or `false` to control if
   *   all schemes are allowed - default is `false` (i.e. all schemes are NOT
   *   allowed).
   * - Variable `schemes` can be set to a comma delimited list of valid
   *   schemes. This value will be ignored if `allowallschemes` is set to `true`.
   *   Default schemes allowed are "http", "https" and "ftp" if this variable
   *   is not specified.
   */
  public static boolean validateUrl(
    Object bean,
    ValidatorAction validatorAction,
    Field field,
    ActionMessages errors,
    Validator validator,
    HttpServletRequest request
  ) {
    return false;
  }
}
