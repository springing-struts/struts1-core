package org.apache.struts.config;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

/**
 * An arbitrary key/value pair which can be retrieved by a config class.
 * This facility should eliminate many use cases for subclassing `*Config`
 * classes by providing a mechanism to pass any amount of arbitrary
 * configuration information into an config class.
 * **Example**
 * <pre>
 * <action path="/example" type="com.example.MyAction">
 *   <set-property key="foo" property="bar" />
 * </action>
 * </pre>
 */
public class PropertyConfig {
  public PropertyConfig(
    @JacksonXmlProperty(localName = "property", isAttribute = true)
    String property,
    @JacksonXmlProperty(localName = "value", isAttribute = true)
    String value
  ) {
    this.property = property;
    this.value = value;
  }

  public String getProperty() {
    return property;
  }
  private final String property;

  public String getValue() {
    return value;
  }
  private final String value;
}
