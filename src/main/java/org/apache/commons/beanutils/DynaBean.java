package org.apache.commons.beanutils;

import jakarta.annotation.Nullable;

/**
 * A DynaBean is a Java object that supports properties whose names and data
 * types, as well as values, may be dynamically modified. To the maximum degree
 * feasible, other components of the BeanUtils package will recognize such
 * beans and treat them as standard JavaBeans for the purpose of retrieving and
 * setting property values.
 */
public interface DynaBean {
  /**
   * Returns true if the specified mapped property contains a value for the
   * specified key value. A runtime exception is thrown if there is no property
   * of the specified name.
   */
  boolean contains(String name, String key);

  /**
   * Returns the value of a simple property with the specified name. A runtime
   * exception is thrown if there is no property of the specified name.
   */
  @Nullable Object get(String name);

  /**
   * Returns the value of an indexed property with the specified name. A
   * runtime exception is thrown if there is no indexed property of the
   * specified name or the given index is out of the range of the property.
   */
  @Nullable Object get(String name, int index);

  /**
   * Returns the value of a mapped property with the specified name, or null if
   * there is no value for the specified key. A runtime exception is thrown if
   * there is no mapped property of the specified name.
   */
  @Nullable Object get(String name, String key);

  /**
   * Remove any existing value for the specified key on the specified mapped
   * property. A runtime exception is thrown if there is no property of the
   * specified name.
   */
  void remove(String name, String key);

  /**
   * Set the value of a simple property with the specified name. A runtime
   * exception is thrown if there is no property of the specified name.
   */
  void set(String name, Object value);

  /**
   * Set the value of an indexed property with the specified name. A runtime
   * exception is thrown if there is no indexed property of the specified name.
   */
  void set(String name, int index, Object value);

  /**
   * Set the value of a mapped property with the specified name. A runtime
   * exception is thrown if there is no keyed property of the specified name.
   */
  void set(String name,String key, Object value);
}
