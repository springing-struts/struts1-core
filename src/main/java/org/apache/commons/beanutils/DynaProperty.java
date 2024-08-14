package org.apache.commons.beanutils;

import org.springframework.lang.Nullable;
import springing.util.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static springing.util.ObjectUtils.classFor;
import static springing.util.ObjectUtils.createInstanceOf;

public class DynaProperty<T> {

  public DynaProperty(String name, Class<T> type) {
    this.name = name;
    this.type = type;
  }

  public DynaProperty(String name, String type) {
    this(name, (Class<T>) classFor(type));
  }

  /**
   * The name of this property.
   */
  public String getName() {
    return name;
  }
  private final String name;

  /**
   * The Java class representing the data type of the underlying property values.
   */
  public Class<T> getType() {
    return type;
  }
  private final Class<T> type;

  /**
   * Returns whether this property represents an indexed value (ie an array or List).
   */
  public boolean isIndexed() {
    return type.isArray() || List.class.isAssignableFrom(type);
  }

  /**
   * Returns whether this property represents a keyed value (ie a Map).
   */
  public boolean isKeyed() {
    return Map.class.isAssignableFrom(type);
  }

  public @Nullable T getValueFrom(Map<String, Object> values) {
    var value = (T) values.get(name);
    if (value == null && (isKeyed() || isIndexed())) {
      value = createInstanceOf(type);
      setValueTo(values, value);
    }
    return value;
  }

  public List<Object> getIndexedValueFrom(Map<java.lang.String, Object> values) {
    if (!isIndexed()) throw new IllegalStateException(String.format(
        "%s (type: %s) is not a indexed property.", name, getType()
    ));
    T v = getValueFrom(values);
    if (type.isArray()) {
      return Arrays.asList(v);
    }
    return (List<Object>) v;
  }

  public Map<String, Object> getKeyedValueFrom(Map<java.lang.String, Object> values) {
    if (!isKeyed()) throw new IllegalStateException(String.format(
        "%s (type: %s) is not a map type property.", name, getType()
    ));
    return (Map<String, Object>) getValueFrom(values);
  }

  public void setValueTo(Map<String, Object> values, @Nullable Object value) {
    if (value == null) {
      values.put(name, value);
      return;
    }
    var valueType = value.getClass();
    if (!type.isAssignableFrom(valueType)) throw new IllegalArgumentException(String.format(
        "The type of the assigned value [%s] is not applicable to the type [%s] of the property [%s].",
        valueType.getSimpleName(), getType(), name
    ));
    values.put(name, value);
  }
}
