package org.apache.commons.validator;

import static java.lang.String.format;
import static java.util.Objects.requireNonNullElse;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import org.apache.struts.util.ModuleUtils;
import org.springframework.lang.Nullable;

/**
 * A variable that can be associated with a Field for passing in information
 * to a pluggable validator. Instances of this class are configured with a
 * `var` xml element.
 */
public class Var {

  public Var(
    @JacksonXmlProperty(localName = "var-name") String name,
    @JacksonXmlProperty(localName = "var-value") String value,
    @JacksonXmlProperty(localName = "var-jstype") @Nullable String jsType,
    @JacksonXmlProperty(
      localName = "resource",
      isAttribute = true
    ) @Nullable Boolean resource
  ) {
    this.name = name;
    this.value = value;
    this.jsType = requireNonNullElse(jsType, JSTYPE_STRING);
    this.resource = resource != null;
  }

  @JsonBackReference
  private Field field;

  /**
   * The name of the variable.
   */
  public String getName() {
    return name;
  }

  private final String name;

  /**
   * The value of the variable.
   */
  public String getValue() {
    return value;
  }

  private final String value;

  /**
   * Returns the value of the variable, interpolating any embedded arguments
   * defined for the field.
   */
  public @Nullable String getVarValue() {
    if (!resource) {
      return field.interpolate(value);
    }
    return ModuleUtils.getCurrent()
      .getMessageResources(bundle)
      .getMessage(value);
  }

  /**
   * Returns the JavaScript literal representation of this variable's value.
   */
  public String getVarValueAsJsLiteral() {
    var value = getVarValue();
    if (value == null) {
      return "null";
    }
    return switch (jsType) {
      case JSTYPE_STRING -> "'" + value.replace("'", "\\'") + "'";
      case JSTYPE_INT -> value.replaceAll("[^-+.0-9]", "");
      case JSTYPE_REGEXP -> "/" + value.replace("/", "\\/") + "/";
      default -> throw new IllegalArgumentException(
        format(
          "Unknown jsType [%s] for variable [%s] of the field [%s].",
          jsType,
          name,
          field.getKey()
        )
      );
    };
  }

  /**
   * The JavaScript type of the variable.
   */
  public String getJsType() {
    return jsType;
  }

  private final String jsType;
  public static final String JSTYPE_STRING = "string";
  public static final String JSTYPE_INT = "int";
  public static final String JSTYPE_REGEXP = "regexp";

  /**
   * Whether the value is a resource key or literal value.
   */
  private boolean resource = false;

  /**
   * The resource bundle name.
   */
  @JacksonXmlProperty(isAttribute = true, localName = "bundle")
  private @Nullable String bundle;
}
