package org.apache.commons.validator;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.chain.contexts.ServletActionContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.lang.Nullable;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.lang.String.format;
import static java.lang.String.join;
import static java.util.Objects.requireNonNullElse;
import static org.springframework.util.StringUtils.hasText;
import static springing.util.StringUtils.lowerCamelize;

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
    this.method = method;
    if (className.isEmpty()) {
      this.testMethod = null;
      this.paramTypes = new ArrayList<>();
    }
    else {
      this.paramTypes = loadParamTypes(methodParams);
      this.testMethod = loadTestMethod(className, method, paramTypes);
    }
  }

  /**
   * Gets the name of method being called for the validator action.
   */
  public String getMethod() {
    return method;
  }
  private final String method;

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


  public boolean test(Object target, Field field, ActionMessages errors ) {
    if (testMethod == null) {
      return true;
    }
    var params = buildParamsFrom(target, field, errors);
    try {
      return (Boolean) testMethod.invoke(null, params.toArray());
    } catch (InvocationTargetException e) {
      if (e.getCause() instanceof RuntimeException re) {
        throw re;
      }
      throw new RuntimeException(
        "An error occurred while calling a validator action: " + name, e.getCause()
      );
    } catch (IllegalAccessException e) {
      throw new RuntimeException(
        "An error occurred while calling a validator action: " + name, e
      );
    }
  }

  private List<?> buildParamsFrom(Object target, Field field, ActionMessages errors) {
    return paramTypes.stream().map(paramType -> {
      if (ValidatorAction.class.isAssignableFrom(paramType)) {
        return this;
      }
      if (HttpServletRequest.class.isAssignableFrom(paramType)) {
        return ServletActionContext.current().getRequest();
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

  /**
   * Gets the dependencies of the validator action as a comma separated list of
   * validator names.
   */
  public String getDepends() {
    return join(",", dependencyList);
  }

  /**
   * Returns the dependent validator names as an unmodifiable {@code List}.
   */
  public List<String> getDependencyList() {
    return Collections.unmodifiableList(dependencyList);
  }

  @JacksonXmlProperty(isAttribute = true, localName = "depends")
  public void setDepends(String depends) {
    dependencyList.clear();
    dependencyList.addAll(Arrays.asList(depends.split("\\s*,\\s*")));
  }
  private final List<String> dependencyList = new ArrayList<>();

  /**
   * Gets the JavaScript equivalent of the Java class and method associated
   * with this action.
   */
  public @Nullable String getJavascript() {
    if (javascriptLoaded) {
      return javascript;
    }
    loadJavascript();
    javascriptLoaded = true;
    return javascript;
  }
  private transient @Nullable String javascript;
  private boolean javascriptLoaded = false;

  /**
   * Load the JavaScript function specified by the given path. For this
   * implementation, the `jsFunction` property should contain a fully qualified
   * package and script name, separated by periods, to be loaded from the class
   * loader that created this instance.
   * **TODO**
   * if the path begins with a '/' the path will be interpreted as absolute,
   * and remain unchanged. If this fails then it will attempt to treat the path
   * as a file path. It is assumed the script ends with a '.js'.
   */
  private synchronized void loadJavascript() {
    var jsFilePath = getJsFunction();
    var resource = new ClassPathResource(jsFilePath);
    try (var in = resource.getInputStream()) {
      javascript = new String(in.readAllBytes(), StandardCharsets.UTF_8);
    } catch (IOException e) {
      if (hasText(jsFunction)) throw new IllegalArgumentException(format(
        "Failed to load the javascript resource file from classpath: [%s].", jsFilePath
      ), e);
    }
  }

  /**
   * The class path to be used to retrieve the JavaScript function.
   * **Examples**
   * <pre>{@code
   *   <validator name="tire"
   *     jsFunction="com.yourcompany.project.tireFunction">
   *   </validator>
   * }</pre>
   * Validator will attempt to load
   * `com.yourcompany.project.tireFunction.js` from its class path.
   * <pre>{@code
   *   <validator name="tire" />
   * }</pre>
   * Validator will use the name attribute to try and load
   * `org.apache.commons.validator.javascript.validateTire.js` which is the
   * default JavaScript definition.
   */
  public String getJsFunction() {
    var path = requireNonNullElse(
      jsFunction,
      JS_FUNCTION_BASE_PACKAGE + "." + lowerCamelize("validate", name)
    );
    return path.replace('.', '/') + ".js";
  }
  @JacksonXmlProperty(isAttribute = true, localName = "jsFunction")
  private @Nullable String jsFunction;
  private static final String JS_FUNCTION_BASE_PACKAGE = "org.apache.commons.validator.javascript";

  public boolean isJsUtility() {
    return testMethod == null && jsFunction != null;
  }

  /**
   * Returns the JavaScript function name. This is optional and can be used
   * instead of validator action name for the name of the JavaScript
   * function/object.
   */
  public String getJsFunctionName() {
    return requireNonNullElse(jsFunctionName, getName());
  }
  @JacksonXmlProperty(isAttribute = true, localName = "jsFunctionName")
  private @Nullable String jsFunctionName;

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
    if (className.isEmpty()) {
      return null;
    }
    try {
      var clazz = Class.forName(className);
      return clazz.getMethod(methodName, paramTypes.toArray(Class[]::new));
    } catch (ClassNotFoundException | NoSuchMethodException e) {
      throw new RuntimeException(format(
        "Failed to retrieve the validator method. class: [%s], method: [%s].",
        className, methodName
      ), e);
    }
  }
}
