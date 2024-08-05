package org.apache.commons.validator;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import org.apache.struts.action.ActionMessages;
import org.springframework.lang.Nullable;
import springing.util.ServletRequestUtils;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Contains the information to dynamically create and run a validation method.
 * This is the class representation of a pluggable validator that can be
 * defined in a xml file with the `validator` element.
 * **Note:** The validation method is assumed to be thread safe.
 */
public class ValidatorAction {
  public ValidatorAction(
      @JacksonXmlProperty(isAttribute = true, localName = "name") String name,
      @JacksonXmlProperty(isAttribute = true, localName = "msg") String msg,
      @JacksonXmlProperty(isAttribute = true, localName = "classname") String className,
      @JacksonXmlProperty(isAttribute = true, localName = "method") String method,
      @JacksonXmlProperty(isAttribute = true, localName = "methodParams") @Nullable String methodParams
  ) {
    this.name = name;
    this.msg = msg;
    if (className.isEmpty()) {
      this.testMethod = null;
      this.paramTypes = null;
    }
    else {
      this.paramTypes = loadParamTypes(methodParams);
      this.testMethod = loadTestMethod(className, method, paramTypes);
    }
  }

  /**
   * The name of the validator action.
   */
  public String getName() {
    return name;
  }
  private final String name;

  /**
   * The message associated with the validator action.
   */
  public String getMsg() {
    return msg;
  }
  private final String msg;

  public boolean test(Object target, Field field, ActionMessages errors) {
    if (testMethod == null) {
      return true;
    }
    var params = buildParamsFrom(target, field, errors);
    try {
      return (Boolean) testMethod.invoke(null, params.toArray());
    } catch (IllegalAccessException | InvocationTargetException e) {
      throw new RuntimeException(
        "An error occurred while calling a validator action: " + name, e
      );
    }
  }

  private List<?> buildParamsFrom(
    Object target, Field field, ActionMessages errors
  ) {
    return paramTypes.stream().map(paramType -> {
      if (ValidatorAction.class.isAssignableFrom(paramType)) {
        return this;
      }
      if (HttpServletRequest.class.isAssignableFrom(paramType)) {
        return ServletRequestUtils.getCurrent();
      }
      if (Field.class.isAssignableFrom(paramType)) {
        return field;
      }
      if (ActionMessages.class.isAssignableFrom(paramType)) {
        return errors;
      }
      if (Validator.class.isAssignableFrom(paramType)) {
        return null;
      }
      if (Object.class.equals(paramType)) {
        return target;
      }
      return null;
    }).toList();
  }

  private @Nullable final Method testMethod;
  private final List<Class<?>> paramTypes;

  @JacksonXmlProperty(isAttribute = true, localName = "depends")
  private @Nullable String depends;

  @JacksonXmlProperty(isAttribute = true, localName = "jsFunctionName")
  private @Nullable String jsFunctionName;

  @JacksonXmlProperty(isAttribute = true, localName = "jsFunction")
  private @Nullable String jsFunction;

  private static List<Class<?>> loadParamTypes(
    @Nullable String methodParams
  ) {
    if (methodParams == null || methodParams.isEmpty()) {
      return List.of();
    }
    var paramTypes = new ArrayList<Class<?>>();
    try {
      for (var paramTypeName : methodParams.split("\\s*,\\s*")) {
        paramTypes.add(Class.forName(paramTypeName));
      }
    } catch (ClassNotFoundException e) {
      throw new RuntimeException("Unknown validator method parameter.", e);
    }
    return paramTypes;
  }

  /**
   * Load the test method being called for this validator action.
   */
  private static @Nullable Method loadTestMethod(
    String className, String methodName, List<Class<?>> paramTypes
  ) {
    if (className.isEmpty()) return null;
    try {
      var clazz = Class.forName(className);
     return clazz.getMethod(methodName, paramTypes.toArray(Class[]::new));

    } catch (ClassNotFoundException | NoSuchMethodException e) {
      throw new RuntimeException(String.format(
        "Failed to retrieve validator method. class: [%s], method: [%s].",
        className, methodName
      ), e);
    }
  }
}
