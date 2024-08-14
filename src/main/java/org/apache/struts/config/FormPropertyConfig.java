package org.apache.struts.config;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import org.apache.commons.beanutils.DynaProperty;
import org.springframework.lang.Nullable;

/**
 * A JavaBean representing the configuration information of a `<form-property>`
 * element in a Struts configuration
 */
public class FormPropertyConfig {

  FormPropertyConfig(
    @JacksonXmlProperty(localName = "name", isAttribute = true) String name,
    @JacksonXmlProperty(localName = "type", isAttribute = true) String type
  ) {
    property = new DynaProperty<>(name, type);

  }

  public DynaProperty<?> getProperty() {
    return property;
  }
  private final DynaProperty<?> property;

  /**
   * The JavaBean property name of the property described by this element.
   */
  public String getName() {
    return property.getName();
  }

  /**
   * The fully qualified Java class name of the implementation class of this
   * bean property, optionally followed by `[]` to indicate that the property
   * is indexed.
   */
  public String getType() {
    return property.getType().getSimpleName();
  }

  /**
   * String representation of the initial value for this property.
   */
  public @Nullable String getInitial() {
    return initial;
  }
  private @Nullable String initial;

  /**
   * The conditions under which the property described by this element should
   * be reset to its <code>initial</code> value when the form's `reset` method
   * is called.
   * This may be set to true (to always reset the property) or a
   * comma-separated list of HTTP request methods.
   */
  public @Nullable String getReset() {
    return reset;
  }
  private @Nullable String reset;

  /**
   * The size of the array to be created if this property is an array type and
   * there is no specified `initial` value. This value must be non-negative.
   */
  public int getSize() {
    return size;
  }
  private int size = 0;
}
