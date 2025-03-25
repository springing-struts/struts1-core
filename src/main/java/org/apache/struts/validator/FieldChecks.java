package org.apache.struts.validator;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

import java.math.RoundingMode;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.*;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.validator.Field;
import org.apache.commons.validator.Validator;
import org.apache.commons.validator.ValidatorAction;
import org.apache.struts.action.ActionMessages;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.lang.Nullable;

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
    var value = Objects.toString(field.getValueOf(bean), "");
    var valid = !value.isBlank();
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
    var fieldVars = field.getVars();
    var joinType = field.getRequiredVarValue("fieldJoin", "AND").toUpperCase();
    var andJoin = joinType.equals("AND");
    var orJoin = joinType.equals("OR");
    if (!andJoin && !orJoin) throw new IllegalArgumentException(
      format("Unknown requiredIf validation join type: [%s].", joinType)
    );
    for (int i = 0; fieldVars.containsKey("field[" + i + "]"); i++) {
      var fieldName = field.getRequiredVarValue("field[" + i + "]");
      var testType = field
        .getRequiredVarValue("fieldTest[" + i + "]")
        .toUpperCase();
      var testNull = testType.equals("NULL");
      var testNotNull = testType.equals("NOTNULL");
      var testEqual = testType.equals("EQUAL");
      if (
        !testNull && !testNotNull && !testEqual
      ) throw new IllegalArgumentException(
        format("Unknown requiredIf validation test type: [%s].", testType)
      );
      var isIndexed = field.getRequiredVarValueAsBool(
        "fieldIndexed[" + i + "]",
        false
      );
      var testField = requireNonNull(
        field.getForm().getFieldByName(fieldName),
        () ->
          format(
            "Unknown field name [%s] of the form [%s] is referred by a requiredIf validator.",
            fieldName,
            field.getForm().getName()
          )
      );
      var testValue = testField.getValueOf(bean);
      var testPassed = testNull
        ? testValue == null
        : testNotNull
          ? testValue != null
          : Objects.equals(
            testValue,
            field.getRequiredVarValue("fieldValue[" + i + "]")
          );
      if (orJoin && testPassed) {
        return validateRequired(
          bean,
          validatorAction,
          field,
          errors,
          validator,
          request
        );
      }
      if (andJoin && !testPassed) {
        return true;
      }
    }
    if (orJoin) {
      return true;
    }
    return validateRequired(
      bean,
      validatorAction,
      field,
      errors,
      validator,
      request
    );
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
    var value = Objects.toString(field.getValueOf(bean), "");
    if (value.isEmpty()) {
      return true;
    }
    var minLength = field.getRequiredVarValueAsLong("minlength");
    var valid = minLength <= value.length();
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
    var value = Objects.toString(field.getValueOf(bean), "");
    if (value.isEmpty()) {
      return true;
    }
    var maxLength = field.getRequiredVarValueAsLong("maxlength");
    var valid = value.length() <= maxLength;
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
    var value = Objects.toString(field.getValueOf(bean), "");
    if (value.isEmpty()) {
      return true;
    }
    var pattern = field.getRequiredVarValue("mask");
    var valid = value.matches(pattern);
    if (!valid) {
      errors.addValidationError(field, validatorAction);
    }
    return valid;
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
    return doValidateByte(
      bean,
      validatorAction,
      field,
      errors,
      validator,
      request,
      null
    );
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
    return doValidateByte(
      bean,
      validatorAction,
      field,
      errors,
      validator,
      request,
      LocaleContextHolder.getLocale()
    );
  }

  private static boolean doValidateByte(
    Object bean,
    ValidatorAction validatorAction,
    Field field,
    ActionMessages errors,
    Validator validator,
    HttpServletRequest request,
    @Nullable Locale locale
  ) {
    var value = Objects.toString(field.getValueOf(bean), "");
    if (value.isEmpty()) {
      return true;
    }
    var isValid = isValidNumber(
      value,
      false,
      Byte.MIN_VALUE,
      Byte.MAX_VALUE,
      locale
    );
    if (isValid) {
      return true;
    }
    errors.addValidationError(field, validatorAction);
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
    return doValidateShort(
      bean,
      validatorAction,
      field,
      errors,
      validator,
      request,
      null
    );
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
    return doValidateShort(
      bean,
      va,
      field,
      errors,
      validator,
      request,
      LocaleContextHolder.getLocale()
    );
  }

  private static boolean doValidateShort(
    Object bean,
    ValidatorAction validatorAction,
    Field field,
    ActionMessages errors,
    Validator validator,
    HttpServletRequest request,
    @Nullable Locale locale
  ) {
    var value = Objects.toString(field.getValueOf(bean), "");
    if (value.isEmpty()) {
      return true;
    }
    var isValid = isValidNumber(
      value,
      false,
      Short.MIN_VALUE,
      Short.MAX_VALUE,
      locale
    );
    if (isValid) {
      return true;
    }
    errors.addValidationError(field, validatorAction);
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
    return doValidateInteger(
      bean,
      validatorAction,
      field,
      errors,
      validator,
      request,
      null
    );
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
    return doValidateInteger(
      bean,
      validatorAction,
      field,
      errors,
      validator,
      request,
      LocaleContextHolder.getLocale()
    );
  }

  /**
   * Checks if the field can safely be converted to an int primitive.
   */
  private static boolean doValidateInteger(
    Object bean,
    ValidatorAction validatorAction,
    Field field,
    ActionMessages errors,
    Validator validator,
    HttpServletRequest request,
    @Nullable Locale locale
  ) {
    var value = Objects.toString(field.getValueOf(bean), "");
    if (value.isEmpty()) {
      return true;
    }
    var isValid = isValidNumber(
      value,
      false,
      Integer.MIN_VALUE,
      Integer.MAX_VALUE,
      locale
    );
    if (isValid) {
      return true;
    }
    errors.addValidationError(field, validatorAction);
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
    return doValidateLong(
      bean,
      validatorAction,
      field,
      errors,
      validator,
      request,
      null
    );
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
    return doValidateLong(
      bean,
      validatorAction,
      field,
      errors,
      validator,
      request,
      LocaleContextHolder.getLocale()
    );
  }

  private static boolean doValidateLong(
    Object bean,
    ValidatorAction validatorAction,
    Field field,
    ActionMessages errors,
    Validator validator,
    HttpServletRequest request,
    @Nullable Locale locale
  ) {
    var value = Objects.toString(field.getValueOf(bean), "");
    if (value.isEmpty()) {
      return true;
    }
    var isValid = isValidNumber(
      value,
      false,
      Long.MIN_VALUE,
      Long.MAX_VALUE,
      locale
    );
    if (isValid) {
      return true;
    }
    errors.addValidationError(field, validatorAction);
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
    return doValidateFloat(
      bean,
      validatorAction,
      field,
      errors,
      validator,
      request,
      null
    );
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
    return doValidateFloat(
      bean,
      validatorAction,
      field,
      errors,
      validator,
      request,
      LocaleContextHolder.getLocale()
    );
  }

  private static boolean doValidateFloat(
    Object bean,
    ValidatorAction validatorAction,
    Field field,
    ActionMessages errors,
    Validator validator,
    HttpServletRequest request,
    @Nullable Locale locale
  ) {
    var value = Objects.toString(field.getValueOf(bean), "");
    if (value.isEmpty()) {
      return true;
    }
    var isValid = isValidNumber(
      value,
      true,
      -Float.MAX_VALUE,
      Float.MAX_VALUE,
      locale
    );
    if (isValid) {
      return true;
    }
    errors.addValidationError(field, validatorAction);
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
    return doValidateDouble(
      bean,
      validatorAction,
      field,
      errors,
      validator,
      request,
      null
    );
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
    return doValidateDouble(
      bean,
      validatorAction,
      field,
      errors,
      validator,
      request,
      LocaleContextHolder.getLocale()
    );
  }

  /**
   * Checks if the field can safely be converted to a double primitive.
   */
  public static boolean doValidateDouble(
    Object bean,
    ValidatorAction validatorAction,
    Field field,
    ActionMessages errors,
    Validator validator,
    HttpServletRequest request,
    @Nullable Locale locale
  ) {
    var value = Objects.toString(field.getValueOf(bean), "");
    if (value.isEmpty()) {
      return true;
    }
    var isValid = isValidNumber(
      value,
      true,
      -Double.MAX_VALUE,
      Double.MAX_VALUE,
      null
    );
    if (isValid) {
      return true;
    }
    errors.addValidationError(field, validatorAction);
    return false;
  }

  private static boolean isValidNumber(
    String value,
    boolean allowsDecimalPoint,
    Number min,
    Number max,
    @Nullable Locale locale
  ) {
    var validNumber = toValidNumber(
      value,
      allowsDecimalPoint,
      min,
      max,
      locale
    );
    return validNumber != null;
  }

  private static @Nullable Number toValidNumber(
    String value,
    boolean allowsDecimalPoint,
    Number min,
    Number max,
    @Nullable Locale locale
  ) {
    var format = locale == null
      ? NumberFormat.getInstance()
      : NumberFormat.getInstance(locale);
    format.setParseIntegerOnly(!allowsDecimalPoint);
    format.setRoundingMode(RoundingMode.UNNECESSARY);
    var position = new ParsePosition(0);
    var v = value.replace("+", "").replace('e', 'E');
    var number = format.parse(v, position);
    if (position.getIndex() != v.length() || position.getErrorIndex() != -1) {
      return null;
    }
    if (allowsDecimalPoint && number instanceof Double d) {
      if (d.isNaN()) {
        return null;
      }
      var inRange = min.doubleValue() <= d && d <= max.doubleValue();
      return inRange ? d : null;
    }
    if (number instanceof Long l) {
      var inRange = min.longValue() <= l && l <= max.longValue();
      return inRange ? l : null;
    }
    return null;
  }

  /**
   * Checks if the field is a valid date. If the field has a datePattern
   * variable, that will be used to format `java.text.SimpleDateFormat`. If the
   * field has a datePatternStrict variable, that will be used to format
   * `java.text.SimpleDateFormat` and the length will be checked so '2/12/1999'
   * will not pass validation with the format 'MM/dd/yyyy' because the month
   * isn't two digits. If no datePattern variable is specified, then the field
   * gets the `DateFormat.SHORT` format for the locale. The setLenient method
   * is set to `false` for all variations.
   */
  public static boolean validateDate(
    Object bean,
    ValidatorAction validatorAction,
    Field field,
    ActionMessages errors,
    Validator validator,
    HttpServletRequest request
  ) {
    var value = Objects.toString(field.getValueOf(bean), "");
    if (value.isEmpty()) {
      return true;
    }
    var datePattern = field.getVarValue("datePattern");
    var datePatternStrict = field.getVarValue("datePatternStrict");
    if (
      datePattern != null && datePatternStrict != null
    ) throw new IllegalArgumentException(
      format(
        "Only one of the properties datePattern or datePatternStrict is allowed" +
        "for date validation of the field [%s].",
        field.getKey()
      )
    );
    var format = datePattern != null
      ? new SimpleDateFormat(datePattern)
      : datePatternStrict != null
        ? new SimpleDateFormat(datePatternStrict)
        : getDefaultDateFormat();
    format.setLenient(false);
    try {
      format.parse(value);
      if (datePatternStrict == null) {
        return true;
      }
      if (value.length() == datePatternStrict.length()) {
        return true;
      }
    } catch (ParseException e) {
      // was invalid date;
    }
    errors.addValidationError(field, validatorAction);
    return false;
  }

  private static DateFormat getDefaultDateFormat() {
    return DateFormat.getDateTimeInstance(
      DateFormat.SHORT,
      DateFormat.SHORT,
      LocaleContextHolder.getLocale()
    );
  }

  /**
   * Checks if a fields value is within a range (`min` and `max` specified in
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
    var value = Objects.toString(field.getValueOf(bean), "");
    if (value.isEmpty()) {
      return true;
    }
    var number = toValidNumber(
      value,
      false,
      Integer.MIN_VALUE,
      Integer.MAX_VALUE,
      null
    );
    if (number == null) {
      errors.addValidationError(field, validatorAction);
      return false;
    }
    var min = field.getVarValueAsLong("min");
    var max = field.getVarValueAsLong("max");
    var inRange =
      (min == null || number.intValue() >= min) &&
      (max == null || number.intValue() <= max);
    if (inRange) {
      return true;
    }
    errors.addValidationError(field, validatorAction);
    return false;
  }

  /**
   * Checks if a fields value is within a range (`min` and `max` specified in
   * the vars attribute).
   */
  public static boolean validateLongRange(
    Object bean,
    ValidatorAction validatorAction,
    Field field,
    ActionMessages errors,
    Validator validator,
    HttpServletRequest request
  ) {
    var value = Objects.toString(field.getValueOf(bean), "");
    if (value.isEmpty()) {
      return true;
    }
    var number = toValidNumber(
      value,
      false,
      Long.MIN_VALUE,
      Long.MAX_VALUE,
      null
    );
    if (number == null) {
      errors.addValidationError(field, validatorAction);
      return false;
    }
    var min = field.getVarValueAsLong("min");
    var max = field.getVarValueAsLong("max");
    var inRange =
      (min == null || number.intValue() >= min) &&
      (max == null || number.intValue() <= max);
    if (inRange) {
      return true;
    }
    errors.addValidationError(field, validatorAction);
    return false;
  }

  /**
   * Checks if a fields value is within a range (`min` and `max` specified in
   * the vars attribute).
   */
  public static boolean validateFloatRange(
    Object bean,
    ValidatorAction validatorAction,
    Field field,
    ActionMessages errors,
    Validator validator,
    HttpServletRequest request
  ) {
    var value = Objects.toString(field.getValueOf(bean), "");
    if (value.isEmpty()) {
      return true;
    }
    var number = toValidNumber(
      value,
      true,
      -Float.MAX_VALUE,
      Float.MAX_VALUE,
      null
    );
    if (number == null) {
      errors.addValidationError(field, validatorAction);
      return false;
    }
    var min = field.getVarValueAsNumber("min");
    var max = field.getVarValueAsNumber("max");
    var inRange =
      (min == null || number.floatValue() >= min.floatValue()) &&
      (max == null || number.floatValue() <= max.floatValue());
    if (inRange) {
      return true;
    }
    errors.addValidationError(field, validatorAction);
    return false;
  }

  /**
   * Checks if a fields value is within a range (`min` and `max` specified in
   * the vars attribute).
   */
  public static boolean validateDoubleRange(
    Object bean,
    ValidatorAction validatorAction,
    Field field,
    ActionMessages errors,
    Validator validator,
    HttpServletRequest request
  ) {
    var value = Objects.toString(field.getValueOf(bean), "");
    if (value.isEmpty()) {
      return true;
    }
    var number = toValidNumber(
      value,
      true,
      -Double.MAX_VALUE,
      Double.MAX_VALUE,
      null
    );
    if (number == null) {
      errors.addValidationError(field, validatorAction);
      return false;
    }
    var min = field.getVarValueAsNumber("min");
    var max = field.getVarValueAsNumber("max");
    var inRange =
      (min == null || number.doubleValue() >= min.doubleValue()) &&
      (max == null || number.doubleValue() <= max.doubleValue());
    if (inRange) {
      return true;
    }
    errors.addValidationError(field, validatorAction);
    return false;
  }

  /**
   * Checks if the field is a valid credit card number.
   * **NOTE**
   * This implementation is based on the OWASP recommendation explained in the following document:
   * <a href="https://owasp.org/www-community/OWASP_Validation_Regex_Repository">
   * OWASP Validation Regex Repository
   * </a>
   */
  public static boolean validateCreditCard(
    Object bean,
    ValidatorAction validatorAction,
    Field field,
    ActionMessages errors,
    Validator validator,
    HttpServletRequest request
  ) {
    var value = Objects.toString(field.getValueOf(bean), "");
    if (value.isEmpty()) {
      return true;
    }
    var m = VALID_CREDIT_CARD_NUMBER.matcher(value);
    if (m.matches()) {
      return true;
    }
    errors.addValidationError(field, validatorAction);
    return false;
  }

  private static final Pattern VALID_CREDIT_CARD_NUMBER = Pattern.compile(
    "^((4\\d{3})|(5[1-5]\\d{2})|(6011)|(7\\d{3}))-?\\d{4}-?\\d{4}-?\\d{4}|3[4,7]\\d{13}$"
  );

  /**
   * Checks if a field has a valid e-mail address.
   * **NOTE**
   * This implementation is based on the OWASP recommendation explained in the following document:
   * <a href="https://owasp.org/www-community/OWASP_Validation_Regex_Repository">
   * OWASP Validation Regex Repository
   * </a>
   */
  public static boolean validateEmail(
    Object bean,
    ValidatorAction validatorAction,
    Field field,
    ActionMessages errors,
    Validator validator,
    HttpServletRequest request
  ) {
    var value = Objects.toString(field.getValueOf(bean), "");
    if (value.isEmpty()) {
      return true;
    }
    var m = VALID_EMAIL.matcher(value);
    if (m.matches()) {
      return true;
    }
    errors.addValidationError(field, validatorAction);
    return false;
  }

  private static final Pattern VALID_EMAIL = Pattern.compile(
    "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}$"
  );

  /**
   * Checks if a field has a valid url. Four optional variables can be
   * specified to configure url validation.
   * - Variable `allow2slashes` can be set to `true` or `false` to control
   *   whether two slashes are allowed - default is `false` (i.e. two slashes
   *   are NOT allowed).
   * - Variable `nofragments` can be set to `true` or `false` to control
   *   whether fragments are allowed - default is `false` (i.e. fragments ARE
   *   allowed).
   * - Variable `allowallschemes` can be set to `true` or `false` to control if
   *   all schemes are allowed - default is `false` (i.e. all schemes are NOT
   *   allowed).
   * - Variable `schemes` can be set to a comma-delimited list of valid
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
    var value = Objects.toString(field.getValueOf(bean), "");
    if (value.isEmpty()) {
      return true;
    }
    try {
      var url = new URI(value).toURL();
      return true;
    } catch (
      URISyntaxException | MalformedURLException | IllegalArgumentException e
    ) {
      // was invalid URL
    }
    errors.addValidationError(field, validatorAction);
    return false;
  }
}
