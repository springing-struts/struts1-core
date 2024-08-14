package org.apache.commons.beanutils;

import jakarta.annotation.Nullable;
import java.util.Map;

public interface MapBackedDynaBean extends DynaBean {

  String getName();
  Map<String, DynaProperty<?>> getPropertiesByPropertyName();
  Map<String, Object> getValuesByPropertyName();

  private DynaProperty<?> propertyOf(String name) {
    var prop = getPropertiesByPropertyName().get(name);
    if (prop == null) throw new IllegalArgumentException(String.format(
        "Form bean [%s] does not have a property of name [%s].",
        getName(), name
    ));
    return prop;
  }

  /**
   * Returns true if the specified mapped property contains a value for the
   * specified key value. A runtime exception is thrown if there is no property
   * of the specified name.
   */
  default boolean contains(String name, String key) {
    var values = getValuesByPropertyName();
    return propertyOf(name).getKeyedValueFrom(values).containsKey(key);
  }

  /**
   * Returns the value of a simple property with the specified name. A runtime
   * exception is thrown if there is no property of the specified name.
   */
  default @Nullable Object get(String name) {
    var values = getValuesByPropertyName();
    return propertyOf(name).getValueFrom(values);
  }

  /**
   * Returns the value of an indexed property with the specified name. A
   * runtime exception is thrown if there is no indexed property of the
   * specified name or the given index is out of the range of the property.
   */
  default @Nullable Object get(String name, int index) {
    var values = getValuesByPropertyName();
    return propertyOf(name).getIndexedValueFrom(values).get(index);
  }

  /**
   * Returns the value of a mapped property with the specified name, or null if
   * there is no value for the specified key. A runtime exception is thrown if
   * there is no mapped property of the specified name.
   */
  default @Nullable Object get(String name, String key) {
    var values = getValuesByPropertyName();
    return propertyOf(name).getKeyedValueFrom(values).get(key);
  }

  /**
   * Remove any existing value for the specified key on the specified mapped
   * property. A runtime exception is thrown if there is no property of the
   * specified name.
   */
  default void remove(String name, String key) {
    var values = getValuesByPropertyName();
    propertyOf(name).getKeyedValueFrom(values).remove(key);
  }

  /**
   * Set the value of a simple property with the specified name. A runtime
   * exception is thrown if there is no property of the specified name.
   */
  default void set(String name, Object value) {
    var values = getValuesByPropertyName();
    propertyOf(name).setValueTo(values, value);
  }

  /**
   * Set the value of an indexed property with the specified name. A runtime
   * exception is thrown if there is no indexed property of the specified name.
   */
  default void set(String name, int index, Object value) {
    var values = getValuesByPropertyName();
    propertyOf(name).getIndexedValueFrom(values).set(index, value);
  }

  /**
   * Set the value of a mapped property with the specified name. A runtime
   * exception is thrown if there is no keyed property of the specified name.
   */
  default void set(String name,String key, Object value) {
    var values = getValuesByPropertyName();
    propertyOf(name).getKeyedValueFrom(values).put(key, value);
  }
}