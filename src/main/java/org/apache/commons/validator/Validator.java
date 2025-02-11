package org.apache.commons.validator;

import java.util.HashMap;
import java.util.Map;

/**
 * Validations are processed by the validate method. An instance of
 * `ValidatorResources` is used to define the validators (validation methods)
 * and the validation rules for a JavaBean.
 */
public class Validator {

  /**
   * Resources key the JavaBean is stored to perform validation on.
   */
  public static final String BEAN_PARAM = "java.lang.Object";

  public Validator(ValidatorResources resources, String formName) {
    this.resources = resources;
    this.formName = formName;
    this.parameters = new HashMap<>();
  }

  /**
   * The Validator Resources.
   */
  private final ValidatorResources resources;

  /**
   * The form name which is the key to a set of validation rules.
   */
  private final String formName;

  /**
   * Maps validation method parameter class names to the objects to be passed
   * into the method.
   */
  private final Map<String, Object> parameters;

  /**
   * Performs validations based on the configured resources.
   */
  public ValidationResults validate() {
    return new ValidationResults();
  }

  public void setBean(Object bean) {
    parameters.put(BEAN_PARAM, bean);
  }

  /**
   * Sets a parameter of a pluggable validation method.
   */
  public void setParameter(String parameterClassName, Object parameterValue) {
    parameters.put(parameterClassName, parameterValue);
  }
}
