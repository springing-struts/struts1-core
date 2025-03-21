package org.apache.struts.config;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import org.springframework.beans.BeanUtils;
import org.springframework.lang.Nullable;

import java.lang.reflect.InvocationTargetException;

import static java.lang.String.format;

/**
 * An arbitrary key/value pair which can be retrieved by a config class.
 * This facility should eliminate many use cases for subclassing `*Config`
 * classes by providing a mechanism to pass any amount of arbitrary
 * configuration information into a config class.
 * Since Struts 1.3, an alternate syntax is supported. By using the "key"
 * attribute instead of the "property" attribute, you can set arbitrary string
 * properties on the Config object which is populated based on the containing
 * element.
 * **Example:**
 * <pre>{@code
 * <action path="/example" type="com.example.MyAction">
 *   <set-property key="foo" property="bar" />
 * </action>
 * }</pre>
 */
public class PropertyConfig {
  public PropertyConfig(
    @JacksonXmlProperty(localName = "property", isAttribute = true)
    @Nullable String property,
    @JacksonXmlProperty(localName = "key", isAttribute = true)
    @Nullable String key,
    @JacksonXmlProperty(localName = "value", isAttribute = true)
    String value
  ) {
    if (property != null && key != null || property == null && key == null) throw new IllegalArgumentException(
      "Only one of the `property` or `key` can be used in the `set-property` element."
    );
    this.property = property;
    this.key = key;
    this.value = value;
  }

  public void applyTo(Object target) {
    var targetClass = target.getClass();
    var property = BeanUtils.getPropertyDescriptor(targetClass, getProperty());
    if (property == null) throw new IllegalArgumentException(format(
      "Unknown property [%s] of the target class [%s]",
      getProperty(), targetClass.getName()
    ));
    var setter = property.getWriteMethod();
    try {
      setter.invoke(target, getValue());
    } catch (InvocationTargetException | IllegalAccessException e) {
      throw new RuntimeException(
        "An error occurred while setting a action mapping property value.", e
      );
    }
  }

  /**
   * Name of the JavaBeans property whose setter method will be called.
   */
  public String getProperty() {
    return property;
  }
  private final String property;

  /**
   * Where supported, the key which will be used to store the specified value
   * in the given config object. Exactly one of "property" or "key" must be
   * specified.
   * **NOTE:**
   *   the "key" attribute is NOT supported for `set-property` inside a
   *   `plug-in` element.
   */
  public String getKey() {
    return key;
  }
  private final String key;

  /**
   * String representation of the value to which this property will be set,
   * after suitable type conversion.
   */
  public String getValue() {
    return value;
  }
  private final String value;
}
