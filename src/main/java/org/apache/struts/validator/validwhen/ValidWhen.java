package org.apache.struts.validator.validwhen;

import jakarta.el.*;
import org.apache.commons.validator.Field;
import org.apache.commons.validator.Validator;
import org.apache.commons.validator.ValidatorAction;
import org.apache.struts.action.ActionMessages;
import org.springframework.lang.Nullable;
import springing.util.ObjectUtils;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;

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
    ActionMessages errors,
    Validator validator,
    HttpServletRequest request
  ) {
    var expression = translateToEL(field.getRequiredVarValue("test"));
    var processor = new ELProcessor();
    processor.getELManager().addBeanNameResolver(new FieldValueResolver(field, bean));
    boolean isValid = processor.eval(expression);
    if (isValid) {
      return true;
    }
    errors.addValidationError(field, validatorAction);
    return false;
  }

  private static String translateToEL(String expression) {
    return expression.replace("*this*", "__this__");
  }

  private static class FieldValueResolver extends BeanNameResolver {

    public FieldValueResolver(Field field, Object bean) {
      this.field = field;
      this.bean = bean;
    }

    private final Field field;
    private final Object bean;

    @Override
    public boolean isNameResolved(String beanName) {
      return true;
    }

    @Override
    public @Nullable Object getBean(String beanName) {
      var propName = beanName.equals("__this__") ? field.getProperty() : beanName;
      var value = ObjectUtils.retrieveValue(bean, propName);
      if (value instanceof String str) {
        if (str.isEmpty()) {
          return null;
        }
        if (str.matches("^([-+])?[0-9]+(\\.[0-9]+([eE][-+]?[0-9]+)?)?$")) {
          return new BigDecimal(str);
        }
      }
      return value;
    }
  }
}
